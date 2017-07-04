package farm.bsg;

import java.util.Map;

import farm.bsg.route.MockRequestBuilder;
import farm.bsg.route.MockRoutingTable;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;

public class PageBootstrap {

    public static enum Method {
        get, post
    }

    public final TestWorld         world;
    public final MultiTenantRouter router;

    private final MockRoutingTable routing;

    public PageBootstrap() throws Exception {
        this(TestWorld.start().withSampleData().done());
    }

    private PageBootstrap(final TestWorld world) throws Exception {
        this.world = world;
        this.router = new MultiTenantRouter() {

            @Override
            public ProductEngine findByDomain(final String domain) {
                return world.engine;
            }

            @Override
            public void informRoutingTableBuilt(final RoutingTable routing) {
            }

            @Override
            public boolean isSecure() {
                return true;
            }
        };

        this.routing = new MockRoutingTable();
        Linker.link(this.routing, this.router);
    }

    public String GET(final String uri, final Map<String, String> params) {
        return (String) this.routing.GET(new MockRequestBuilder(uri, this.world.engine).withParams(params).withAdmin());
    }

    public Object POST(final String uri, final Map<String, String> params) {
        return this.routing.POST(new MockRequestBuilder(uri, this.world.engine).withParams(params).withAdmin());
    }

    public QueryEngine query() {
        return this.world.engine;
    }
}
