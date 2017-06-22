package farm.bsg;

import static spark.Spark.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import org.slf4j.Logger;

import com.amazon.speech.Sdk;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import farm.bsg.amazon.AmazonS3StorageLogger;
import farm.bsg.cron.JobManager;
import farm.bsg.data.DiskStorageLogger;
import farm.bsg.data.contracts.PersistenceLogger;
import farm.bsg.ops.Logs;
import farm.bsg.ops.Status;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.spark.SparkRouting;
import spark.Route;
import spark.Service;

public class Server {

    private static final Logger LOG = Logs.of(Server.class);

    private static String getGenericTemplate() throws Exception {
        String html = Server.getTextFile("generic.html");
        return html.replaceAll("\\$CACHE_BUSTER\\$", Long.toHexString(System.currentTimeMillis()));
    }

    public static MultiTenantRouter devRouter() throws Exception {
        JobManager jobManager = new JobManager();
        File devStorage = new File("/farm");
        PersistenceLogger persistence = new DiskStorageLogger(devStorage);
        ProductEngine engine = new ProductEngine(jobManager, persistence, getGenericTemplate());
        ManualRouter router = new ManualRouter(false);
        router.setDefault(engine);
        jobManager.start();
        return router;
    }

    private static ProductEngine prodScope(JobManager jobManager, AmazonS3 s3, String scope) throws Exception {
        PersistenceLogger persistence = new AmazonS3StorageLogger(s3, "state.bsg.farm", "customers/" + scope + "/");
        return new ProductEngine(jobManager, persistence, getGenericTemplate());
    }

    public static MultiTenantRouter prodRouter() throws Exception {
        JobManager jobManager = new JobManager();
        InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider(true);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).withCredentials(provider).build();
        ManualRouter router = new ManualRouter(true);
        router.addDomain("bsg.farm", prodScope(jobManager, s3, "jeffie"));
        router.addDomain("demo.bsg.farm", prodScope(jobManager, s3, "demo"));
        jobManager.start();
        return router;
    }

    public static void main(String[] args) throws Exception {
        LOG.info("server-started");

        ServerOptions options = new ServerOptions(args);
        Service forwarding = ssl_forwarding();

        port(8080);
        get("/ping", (req, res) -> "Hello");

        boolean secure = true;
        MultiTenantRouter router;
        if (options.production) {
            LOG.info("setting up production router");
            router = prodRouter();
        } else {
            LOG.info("setting up development router");
            secure = false;
            router = devRouter();
        }

        LOG.info("mapping style.css");
        expose("style.css");
        LOG.info("mapping code.js");
        expose("code.js");

        LOG.info("mapping favicon.ico");
        favicon();

        LOG.info("mapping manifest.json");
        get("/manifest.json", new Manifest());

        LOG.info("mapping / to -> /sign-in");
        get("/", (req, res) -> {
            res.redirect("/sign-in");
            return null;
        });

        LOG.info("setting up /status");
        Status status = new Status(BsgCounters.I.source);
        get("/status", status);

        LOG.info("setting up /alexa");
        Route alexaRoute = (req, res) -> {
            ProductEngine engine = router.findByDomain(req.headers("Host"));
            if (engine == null) {
                res.status(404);
                return null;
            }
            byte[] input = req.bodyAsBytes();
            String signature = req.headers(Sdk.SIGNATURE_REQUEST_HEADER);
            String certificateChain = req.headers(Sdk.SIGNATURE_CERTIFICATE_CHAIN_URL_REQUEST_HEADER);
            if (engine.alexa.auth(input, signature, certificateChain)) {
                byte[] output = engine.alexa.handle(input);
                return output;
            } else {
                return "NOPE";
            }
        };
        post("/alexa", alexaRoute);
        get("/alexa", alexaRoute);

        LOG.info("building routing table");
        RoutingTable routing = new SparkRouting(router, secure);
        Linker.link(routing, router);
        LOG.info("routing table built");

        LOG.info("installing shutdown hook");
        Runnable shutdownTarget = new Runnable() {
            @Override
            public void run() {
                System.err.println("[draining]");
                forwarding.stop();
                System.err.println("[draining]");
                stop();
            }
        };
        Thread shutdownHook = new Thread(shutdownTarget);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        LOG.info("service ready");
    }

    private static void expose(String name) throws Exception {
        String mimeCss = "text/css";

        final String contentType;
        if (name.endsWith(".css")) {
            contentType = mimeCss;
        } else if (name.endsWith(".js")) {
            contentType = "text/javascript";
        } else {
            contentType = "";
        }

        final String file = getTextFile(name);
        get("/" + name, (req, res) -> {
            res.header("Cache-Control", "public, max-age=3600");
            res.header("Content-Type", contentType);
            return file;
        });
    }

    private static void favicon() throws Exception {
        final byte[] file = getBytes("favicon.ico");
        get("/favicon.ico", (req, res) -> {
            res.header("Cache-Control", "public, max-age=3600");
            res.header("Content-Type", "image/x-icon");
            return file;
        });
    }

    public static byte[] getBytes(String name) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int rd;
            while ((rd = in.read(buffer)) > 0) {
                baos.write(buffer, 0, rd);
            }
            return baos.toByteArray();
        } finally {
            in.close();
        }

    }

    public static String getTextFile(String name) throws Exception {
        return new String(getBytes(name));
    }

    public static Service ssl_forwarding() {
        Service service = Service.ignite();
        service.port(8000);
        Route read = (req, res) -> {
            if (req.uri().equals("/ping")) {
                return "";
            }

            String url = req.url();
            if (url.contains(":8000")) {
                url = url.replaceFirst(":8000", ":8080");
            } else {
                url = url.replaceFirst("http:", "https:");
            }
            res.redirect(url);
            return null;
        };
        service.get("*", read);
        service.options("*", read);
        service.head("*", read);

        Route write = (req, res) -> {
            return "should not happen, why did we a mutation to an insecure port";
        };
        service.post("*", write);
        service.put("*", write);
        service.delete("*", write);
        return service;

    }

}
