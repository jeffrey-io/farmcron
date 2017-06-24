package farm.bsg.route.spark;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;

import com.amazonaws.util.json.Jackson;

import farm.bsg.ProductEngine;
import farm.bsg.data.UriBlobCache.UriBlob;
import farm.bsg.ops.CounterSource;
import farm.bsg.ops.Logs;
import farm.bsg.ops.RequestMetrics;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.AnonymousRoute;
import farm.bsg.route.ControlledURI;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.CustomerRoute;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SessionRoute;
import farm.bsg.route.text.TextMessage;
import spark.Request;
import spark.Response;
import spark.Route;

public class SparkRouting extends RoutingTable {
    private static final Logger     LOG = Logs.of(SparkRouting.class);

    private final MultiTenantRouter router;
    private final boolean           secure;
    private final CounterSource     counterSource;

    public SparkRouting(MultiTenantRouter router, CounterSource counterSource, boolean secure) {
        this.router = router;
        this.counterSource = counterSource;
        this.secure = secure;
        counterSource.setSection("Traffic Routing");
    }

    public String exceptionalize(Exception err) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        err.printStackTrace(writer);
        writer.flush();
        baos.flush();
        return "<h1>" + err.getMessage() + "</h><pre>\n" + new String(baos.toByteArray()) + "</pre>";
    }

    private ProductEngine engineOf(Request request) {
        return router.findByDomain(request.headers("Host"));
    }

    @Override
    public void setupTexting() {
        Route routeTwilio = (req, res) -> {
            log(req);
            ProductEngine engine = engineOf(req);
            if (engine == null) {
                res.status(404);
                return "Engine not found:";
            }
            String message = req.queryParams("Body");
            String from = req.queryParams("From");
            String to = req.queryParams("To");
            String debug = Jackson.toJsonString(req.queryMap().toMap());
            TextMessage text = new TextMessage("twilio", to, from, message, debug);
            TextMessage result = handleText(engine, text);
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Response><Message>" + result.message + "</Message></Response>";
        };
        spark.Spark.post("/texting-twilio", routeTwilio);
        spark.Spark.get("/texting-twilio", routeTwilio);

        Route routeFacebook = new FacebookSparkRoute(router, this);
        spark.Spark.post("/facebook-messenger", routeFacebook);
        spark.Spark.get("/facebook-messenger", routeFacebook);
    }

    @Override
    public void post(ControlledURI path, SessionRoute route) {
        RequestMetrics metrics = counterSource.request("pos", path.toRoutingPattern());
        spark.Spark.post(path.toRoutingPattern(), (req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                SessionRequest sessionRequest = new SessionRequest(engine, sparked);
                if (!sessionRequest.isAllowed()) {
                    sparked.redirect(new FinishedHref("/"));
                    return null;
                }
                log(sessionRequest);
                return localHandle(route.handle(sessionRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);
            }

        });
    }
    
    @Override
    public void get(ControlledURI path, SessionRoute route) {
        RequestMetrics metrics = counterSource.request("get", path.toRoutingPattern());
        spark.Spark.get(path.toRoutingPattern(), (req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                SessionRequest sessionRequest = new SessionRequest(engine, sparked);
                if (!sessionRequest.isAllowed()) {
                    sparked.redirect(new FinishedHref("/"));
                    return null;
                }
                log(sessionRequest);
                return localHandle(route.handle(sessionRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }

    @Override
    public void public_post(ControlledURI path, AnonymousRoute route) {
        RequestMetrics metrics = counterSource.request("post", path.toRoutingPattern());
        spark.Spark.post(path.toRoutingPattern(), (req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return localHandle(route.handle(anonymousRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);
            }
        });
    }

    @Override
    public void public_get(ControlledURI path, AnonymousRoute route) {
        RequestMetrics metrics = counterSource.request("get", path.toRoutingPattern());
        spark.Spark.get(path.toRoutingPattern(), (req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return localHandle(route.handle(anonymousRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }

    private Object localHandle(Object result, Request req, Response resp) {
        if (result == null) {
            return "";
        }
        if (result instanceof UriBlob) {
            UriBlob blob = (UriBlob) result;
            resp.status(200);
            resp.type(blob.contentType);
            return blob.blob;
        }
        return result;
    }

    private void log(Request req) {
        LOG.info("access host:{} uri:{} method:{}", req.headers("Host"), req.uri(), req.requestMethod());
    }

    private void log(SessionRequest session) {
        LOG.info("session person:{}", session.getPerson().get("login"));
    }

    private void log(AnonymousRequest session) {
        LOG.info("anonymous!");
    }

    private void log(CustomerRequest session) {
        LOG.info("customer!");
    }

    @Override
    public void set_404(AnonymousRoute route) {
        RequestMetrics metrics = counterSource.request("get", "404");
        spark.Spark.notFound((req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return localHandle(route.handle(anonymousRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }

    @Override
    public void customer_get(ControlledURI path, CustomerRoute route) {
        RequestMetrics metrics = counterSource.request("get", path.toRoutingPattern());
        spark.Spark.get(path.toRoutingPattern(), (req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                CustomerRequest customerRequest = new CustomerRequest(engine, sparked);
                log(customerRequest);
                return localHandle(route.handle(customerRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }

    @Override
    public void customer_post(ControlledURI path, CustomerRoute route) {
        RequestMetrics metrics = counterSource.request("post", path.toRoutingPattern());
        spark.Spark.post(path.toRoutingPattern(), (req, res) -> {
            RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                CustomerRequest customerRequest = new CustomerRequest(engine, sparked);
                log(customerRequest);
                return localHandle(route.handle(customerRequest), req, res);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }
}
