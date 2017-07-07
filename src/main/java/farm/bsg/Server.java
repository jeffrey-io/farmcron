package farm.bsg;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.impl.OutputStreamLogger;

import com.amazon.speech.Sdk;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;

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

    private static final Logger LOG = makeLogAndCaptureStdOutputs(false);

    public static ProductEngine devEngine() throws Exception {
        final JobManager jobManager = new JobManager();
        final File devStorage = new File("/farm");
        final PersistenceLogger persistence = new DiskStorageLogger(devStorage);
        final ProductEngine engine = new ProductEngine(jobManager, persistence, getGenericTemplate());
        jobManager.start();
        return engine;
    }

    public static MultiTenantRouter devRouter() throws Exception {
        final ManualRouter router = new ManualRouter(false);
        router.setDefault(devEngine());
        return router;
    }

    private static void expose(final String name) throws Exception {
        final String mimeCss = "text/css";

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

    public static byte[] getBytes(final String name) throws Exception {
        final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            int rd;
            while ((rd = in.read(buffer)) > 0) {
                baos.write(buffer, 0, rd);
            }
            return baos.toByteArray();
        } finally {
            in.close();
        }

    }

    private static String getGenericTemplate() throws Exception {
        final String html = Server.getTextFile("generic.html");
        return html.replaceAll("\\$CACHE_BUSTER\\$", Long.toHexString(System.currentTimeMillis()));
    }

    public static String getTextFile(final String name) throws Exception {
        return new String(getBytes(name));
    }

    public static void main(final String[] args) throws Exception {
        LOG.info("server-started");

        final ServerOptions options = new ServerOptions(args);
        final Service forwarding = ssl_forwarding();

        port(8080);
        get("/ping", (req, res) -> "HELLO WORLD");

        boolean secure = true;
        MultiTenantRouter router;
        if (options.production) {
            LOG.info("setting up production router");
            router = prodRouter(options);
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

        LOG.info("setting up /status");
        final Status status = new Status(BsgCounters.I.source);
        get("/status", status);

        LOG.info("setting up /alexa");
        final Route alexaRoute = (req, res) -> {
            final ProductEngine engine = router.findByDomain(req.headers("Host"));
            if (engine == null) {
                res.status(404);
                return null;
            }
            final byte[] input = req.bodyAsBytes();
            final String signature = req.headers(Sdk.SIGNATURE_REQUEST_HEADER);
            final String certificateChain = req.headers(Sdk.SIGNATURE_CERTIFICATE_CHAIN_URL_REQUEST_HEADER);
            if (engine.alexa.auth(input, signature, certificateChain)) {
                final byte[] output = engine.alexa.handle(input);
                return output;
            } else {
                return "NOPE";
            }
        };
        post("/alexa", alexaRoute);
        get("/alexa", alexaRoute);

        LOG.info("building routing table");
        final RoutingTable routing = new SparkRouting(router, BsgCounters.I.source, secure);
        Linker.link(routing, router);
        BsgCounters.I.source.lockDown();
        LOG.info("routing table built");

        LOG.info("installing shutdown hook");
        final Runnable shutdownTarget = () -> {
            LOG.info("[draining ssl forwarding]");
            forwarding.stop();
            LOG.info("[draining main]");
            stop();
        };
        final Thread shutdownHook = new Thread(shutdownTarget);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        LOG.info("service ready");
    }

    private static Logger makeLogAndCaptureStdOutputs(final boolean skip) {
        if (skip) {
            return Logs.of(Server.class);
        }
        final ByteArrayOutputStream stderrOutBytes = new ByteArrayOutputStream();
        final PrintStream stderrOut = new PrintStream(stderrOutBytes);
        System.setErr(stderrOut);
        final Logger logger = Logs.of(Server.class);
        stderrOut.flush();
        System.setErr(new PrintStream(new OutputStreamLogger("STDERR")));
        System.setOut(new PrintStream(new OutputStreamLogger("STDOUT")));
        return logger;
    }

    public static MultiTenantRouter prodRouter(final ServerOptions options) throws Exception {
        final JobManager jobManager = new JobManager();
        final AmazonS3 s3 = options.s3();
        final ManualRouter router = new ManualRouter(true);

        // classic
        LOG.info("Adding classic domain in (we need to do a data migration, which means a reliable backup system)");
        router.addDomain("bsg.farm", prodScope(jobManager, s3, "jeffie", options));

        try {
            final ListObjectsRequest lor = new ListObjectsRequest().withBucketName(options.bucket).withDelimiter("/").withPrefix("customers/");
            for (final String prefix : s3.listObjects(lor).getCommonPrefixes()) {
                final String domain = prefix.split(Pattern.quote("/"))[1];
                if (domain.contains(".")) {
                    LOG.info("Found domain:" + domain);
                    router.addDomain(domain, prodScope(jobManager, s3, domain, options));
                } else {
                    LOG.info("skipping:" + domain + " since it fails the domain check");
                }
            }
        } catch (final Throwable t) {
            LOG.error("error listing objects: {}", t);
        }
        jobManager.start();
        return router;
    }

    private static ProductEngine prodScope(final JobManager jobManager, final AmazonS3 s3, final String scope, final ServerOptions options) throws Exception {
        final PersistenceLogger persistence = new AmazonS3StorageLogger(s3, options.bucket, "customers/" + scope + "/");
        final ProductEngine engine = new ProductEngine(jobManager, persistence, getGenericTemplate());
        if (!engine.siteproperties_get().notifyAdmin("Process Started:" + System.currentTimeMillis())) {
            LOG.error("failed to notify admin");
        }
        return engine;
    }

    public static Service ssl_forwarding() {
        final Service service = Service.ignite();
        service.port(8000);
        final Route read = (req, res) -> {
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

        final Route write = (req, res) -> {
            return "should not happen, why did we a mutation to an insecure port";
        };
        service.post("*", write);
        service.put("*", write);
        service.delete("*", write);
        return service;

    }

}
