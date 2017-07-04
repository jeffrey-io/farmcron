package farm.bsg;

import java.util.Map;

import farm.bsg.route.MockRequestBuilder;
import farm.bsg.route.MockRoutingTable;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;

public class PageBootstrap {

    public final TestWorld         world;
    public final MultiTenantRouter router;
    private final MockRoutingTable routing;

    public PageBootstrap() throws Exception {
        this(TestWorld.start().withSampleData().done());
    }

    public static enum Method {
        get, post
    }

    public QueryEngine query() {
        return world.engine;
    }

    public String GET(String uri, Map<String, String> params) {
        return (String) routing.GET(new MockRequestBuilder(uri, world.engine).withParams(params).withAdmin());
    }

    public Object POST(String uri, Map<String, String> params) {
        return routing.POST(new MockRequestBuilder(uri, world.engine).withParams(params).withAdmin());
    }

    private PageBootstrap(TestWorld world) throws Exception {
        this.world = world;
        this.router = new MultiTenantRouter() {

            @Override
            public boolean isSecure() {
                return true;
            }

            @Override
            public void informRoutingTableBuilt(RoutingTable routing) {
            }

            @Override
            public ProductEngine findByDomain(String domain) {
                return world.engine;
            }
        };

        this.routing = new MockRoutingTable();
        Linker.link(this.routing, this.router);
    }
}
