package farm.bsg.route;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ControlledURI {

    public abstract FinishedHref href(Map<String, String> map);

    public FinishedHref href(final String... args) {
        if (args.length == 0) {
            return href(Collections.emptyMap());
        } else {
            final HashMap<String, String> map = new HashMap<>();
            for (int k = 0; k + 1 < args.length; k += 2) {
                if (args[k + 1] != null) {
                    map.put(args[k], args[k + 1]);
                }
            }
            return href(map);
        }
    }

    public abstract String toRoutingPattern();
}
