package farm.bsg.pages;

import farm.bsg.ops.CounterCodeGen;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.RoutingTable;

public class PublicSite {
    
    private final AnonymousRequest request;
    public PublicSite(AnonymousRequest request) {
        this.request = request;
    }
    
    String render() {
        return "Hello World:" + request.getURI();
    }
    
    public static void link(RoutingTable routing) {
        routing.set_404((as) -> new PublicSite(as).render());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Public Site");
    }
}
