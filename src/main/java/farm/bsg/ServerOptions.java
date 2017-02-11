package farm.bsg;

public class ServerOptions {

    public final boolean production;

    public ServerOptions(String[] args) {
        boolean production_ = false;
        for (int k = 0; k < args.length; k++) {
            String arg = args[k].toLowerCase();
            if (arg.equalsIgnoreCase("--production")) {
                production_ = true;
            }
        }

        this.production = production_;
    }

}
