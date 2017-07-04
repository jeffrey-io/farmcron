package farm.bsg;

import org.slf4j.Logger;

import farm.bsg.ops.Logs;

public class ServerOptions {
    private static final Logger LOG = Logs.of(ServerOptions.class);

    public final boolean        production;
    public final String         bucket;
    public final String         region;

    public ServerOptions(String[] args) {
        LOG.info("server options parsing....");
        boolean production_ = false;
        String bucket_ = null;
        String region_ = "us-west-2";
        for (int k = 0; k < args.length; k++) {
            String arg = args[k].toLowerCase();
            boolean mayHaveArgument = k + 1 < args.length;
            if (arg.equalsIgnoreCase("--production")) {
                production_ = true;
            }
            if (arg.equalsIgnoreCase("--bucket") && mayHaveArgument) {
                bucket_ = args[k + 1];
                k++;
                continue;
            }
            if (arg.equalsIgnoreCase("--region") && mayHaveArgument) {
                region_ = args[k + 1];
                k++;
                continue;
            }
        }

        this.production = production_;
        this.bucket = bucket_;
        this.region = region_;

        if (production) {
            LOG.info("production mode enabled...");
        } else {
            LOG.info("dev mode enabled...");
        }
        LOG.info("s3 options...");
        LOG.info("  bucket:" + bucket);
        LOG.info("  region:" + region);
        LOG.info("server options parsed...");
    }
}
