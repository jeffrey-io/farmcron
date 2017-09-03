package farm.bsg.route.spark;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;

import com.amazonaws.util.json.Jackson;

import farm.bsg.ProductEngine;
import farm.bsg.data.UriBlobCache.UriBlob;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterSource;
import farm.bsg.ops.Logs;
import farm.bsg.ops.RequestMetrics;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.AnonymousRoute;
import farm.bsg.route.ApiAction;
import farm.bsg.route.ApiRequest;
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

    public SparkRouting(final MultiTenantRouter router, final CounterSource counterSource, final boolean secure) {
        this.router = router;
        this.counterSource = counterSource;
        this.secure = secure;
        counterSource.setSection("Traffic Routing");
    }

    @Override
    public void api_post(final ControlledURI path, final ApiAction route) {
        final RequestMetrics metrics = this.counterSource.request("post", path.toRoutingPattern());
        Route sparkRoute = (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final ProductEngine engine = engineOf(req);
                final String deviceToken = sparked.getParam("token");
                if (deviceToken == null) {
                    return exceptionalize(new IllegalStateException("no device"));
                }
                final Person person = engine.auth.authenticateByDeviceToken(deviceToken);
                if (person == null) {
                    return exceptionalize(new IllegalStateException("invalid device token"));
                } else {
                    final ApiRequest request = new ApiRequest(engine, sparked, person);
                    return localHandle(route.handle(request), req, res);
                }
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        };
        spark.Spark.post(path.toRoutingPattern(), sparkRoute);
        spark.Spark.get(path.toRoutingPattern(), sparkRoute);

    }

    @Override
    public void customer_get(final ControlledURI path, final CustomerRoute route) {
        final RequestMetrics metrics = this.counterSource.request("get", path.toRoutingPattern());
        spark.Spark.get(path.toRoutingPattern(), (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final CustomerRequest customerRequest = new CustomerRequest(engine, sparked);
                log(customerRequest);
                return localHandle(route.handle(customerRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    @Override
    public void customer_post(final ControlledURI path, final CustomerRoute route) {
        final RequestMetrics metrics = this.counterSource.request("post", path.toRoutingPattern());
        spark.Spark.post(path.toRoutingPattern(), (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final CustomerRequest customerRequest = new CustomerRequest(engine, sparked);
                log(customerRequest);
                return localHandle(route.handle(customerRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    private ProductEngine engineOf(final Request request) {
        return this.router.findByDomain(request.headers("Host"));
    }

    public String exceptionalize(final Exception err) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(baos);
        err.printStackTrace(writer);
        writer.flush();
        baos.flush();
        return "<h1>" + err.getMessage() + "</h><pre>\n" + new String(baos.toByteArray()) + "</pre>";
    }

    @Override
    public void get(final ControlledURI path, final SessionRoute route) {
        final RequestMetrics metrics = this.counterSource.request("get", path.toRoutingPattern());
        spark.Spark.get(path.toRoutingPattern(), (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final SessionRequest sessionRequest = new SessionRequest(engine, sparked);
                if (!sessionRequest.isAllowed()) {
                    LOG.info("not allowed request!");
                    sparked.redirect(new FinishedHref("/"));
                    return null;
                }
                log(sessionRequest);
                return localHandle(route.handle(sessionRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    private Object localHandle(final Object result, final Request req, final Response resp) {
        if (result == null) {
            return "";
        }
        if (result instanceof UriBlob) {
            final UriBlob blob = (UriBlob) result;
            resp.status(200);
            resp.type(blob.contentType);
            return blob.blob;
        }
        return result;
    }

    private void log(final AnonymousRequest session) {
        LOG.info("anonymous!");
    }

    private void log(final CustomerRequest session) {
        LOG.info("customer!");
    }

    private void log(final Request req) {
        LOG.info("access host:{} uri:{} method:{}", req.headers("Host"), req.uri(), req.requestMethod());
    }

    private void log(final SessionRequest session) {
        LOG.info("session person:{} allowed:{}", session.getPerson().get("login"), session.isAllowed());
    }

    @Override
    public void post(final ControlledURI path, final SessionRoute route) {
        final RequestMetrics metrics = this.counterSource.request("pos", path.toRoutingPattern());
        spark.Spark.post(path.toRoutingPattern(), (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final SessionRequest sessionRequest = new SessionRequest(engine, sparked);
                if (!sessionRequest.isAllowed()) {
                    sparked.redirect(new FinishedHref("/"));
                    return null;
                }
                log(sessionRequest);
                return localHandle(route.handle(sessionRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    @Override
    public void public_get(final ControlledURI path, final AnonymousRoute route) {
        final RequestMetrics metrics = this.counterSource.request("get", path.toRoutingPattern());
        spark.Spark.get(path.toRoutingPattern(), (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return localHandle(route.handle(anonymousRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    @Override
    public void public_post(final ControlledURI path, final AnonymousRoute route) {
        final RequestMetrics metrics = this.counterSource.request("post", path.toRoutingPattern());
        spark.Spark.post(path.toRoutingPattern(), (req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return localHandle(route.handle(anonymousRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    @Override
    public void set_404(final AnonymousRoute route) {
        final RequestMetrics metrics = this.counterSource.request("get", "404");
        spark.Spark.notFound((req, res) -> {
            final RequestMetrics.InflightRequest local = metrics.begin();
            try {
                log(req);
                final ProductEngine engine = engineOf(req);
                final SparkBox sparked = new SparkBox(req, res, this.secure);
                final AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return localHandle(route.handle(anonymousRequest), req, res);
            } catch (final Exception err) {
                return exceptionalize(err);
            } finally {
                local.complete(true);
            }
        });
    }

    @Override
    public void setupTexting() {
        final Route routeTwilio = (req, res) -> {
            log(req);
            final ProductEngine engine = engineOf(req);
            if (engine == null) {
                res.status(404);
                return "Engine not found:";
            }
            final String message = req.queryParams("Body");
            final String from = req.queryParams("From");
            final String to = req.queryParams("To");
            final String debug = Jackson.toJsonString(req.queryMap().toMap());
            final TextMessage text = new TextMessage("twilio", to, from, message, debug);
            final TextMessage result = handleText(engine, text);
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Response><Message>" + result.message + "</Message></Response>";
        };
        spark.Spark.post("/texting-twilio", routeTwilio);
        spark.Spark.get("/texting-twilio", routeTwilio);

        final Route routeFacebook = new FacebookSparkRoute(this.router, this);
        spark.Spark.post("/facebook-messenger", routeFacebook);
        spark.Spark.get("/facebook-messenger", routeFacebook);
    }
}
