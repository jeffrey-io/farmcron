package farm.bsg.route.spark;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;

import com.amazonaws.util.json.Jackson;

import farm.bsg.ProductEngine;
import farm.bsg.ops.Logs;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.AnonymousRoute;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SessionRoute;
import farm.bsg.route.text.TextMessage;
import spark.Request;
import spark.Route;

public class SparkRouting extends RoutingTable {
    private static final Logger     LOG = Logs.of(SparkRouting.class);

    private final MultiTenantRouter router;
    private final boolean           secure;

    public SparkRouting(MultiTenantRouter router, boolean secure) {
        this.router = router;
        this.secure = secure;
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
    public void post(String path, SessionRoute route) {
        spark.Spark.post(path, (req, res) -> {
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                SessionRequest sessionRequest = new SessionRequest(engine, sparked);
                if (!sessionRequest.isAllowed()) {
                    sparked.redirect("/");
                    return null;
                }
                log(sessionRequest);
                return route.handle(sessionRequest);
            } catch (Exception err) {
                return exceptionalize(err);
            }

        });
    }

    @Override
    public void get(String path, SessionRoute route) {
        spark.Spark.get(path, (req, res) -> {
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                SessionRequest sessionRequest = new SessionRequest(engine, sparked);
                if (!sessionRequest.isAllowed()) {
                    sparked.redirect("/");
                    return null;
                }
                log(sessionRequest);
                return route.handle(sessionRequest);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }
    

    @Override
    public void public_post(String path, AnonymousRoute route) {
        spark.Spark.post(path, (req, res) -> {
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return route.handle(anonymousRequest);
            } catch (Exception err) {
                return exceptionalize(err);
            }
        });
    }

    @Override
    public void public_get(String path, AnonymousRoute route) {
        spark.Spark.get(path, (req, res) -> {
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return route.handle(anonymousRequest);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
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
    
    @Override
    public void set_404(AnonymousRoute route) {
        spark.Spark.notFound((req, res) -> {
            try {
                log(req);
                ProductEngine engine = engineOf(req);
                SparkBox sparked = new SparkBox(req, res, secure);
                AnonymousRequest anonymousRequest = new AnonymousRequest(engine, sparked);
                log(anonymousRequest);
                return route.handle(anonymousRequest);
            } catch (Exception err) {
                return exceptionalize(err);

            }
        });
    }
}
