package farm.bsg;

import java.util.HashMap;

public class PageTesting {

    public PageBootstrap go() throws Exception {
        return new PageBootstrap();
    }

    public HashMap<String, String> params(final String... kvp) {
        final HashMap<String, String> map = new HashMap<>();
        for (int k = 0; k + 1 < kvp.length; k++) {
            map.put(kvp[k], kvp[k + 1]);
        }
        return map;
    }
}
