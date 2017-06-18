package farm.bsg;

import java.util.Map;

import org.junit.Assert;

import farm.bsg.data.Authenticator.AuthResult;
import farm.bsg.route.BinaryFile;
import farm.bsg.route.MockRequestBuilder;
import farm.bsg.route.MockRoutingTable;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RequestResponseWrapper;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SessionRoute;

public class PageBootstrap {

    public final TestWorld                        world;
    public final MultiTenantRouter                router;
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

    private String redirectedUri = null;

    private Object execute(String uri, SessionRoute route, Map<String, String> params) {
        Assert.assertNotNull(uri, route);
        AuthResult result = world.engine.auth.authenticateByUsernameAndPassword("admin", "password");
        Assert.assertTrue(result.allowed);

        RequestResponseWrapper request = new RequestResponseWrapper() {

            @Override
            public BinaryFile getFile(String key) {
                return null;
            }

            @Override
            public String getURI() {
                return uri;
            }
            
            @Override
            public void setCookie(String key, String value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void redirect(String uri) {
                redirectedUri = uri;
            }

            @Override
            public boolean hasNonNullQueryParam(String key) {
                return getParam(key) != null;
            }

            @Override
            public String[] getParamList(String key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getParam(String key) {
                return params.get(key);
            }

            @Override
            public String getCookie(String key) {
                if (key.equals("xs")) {
                    return result.cookie;
                }
                return null;
            }
        };

        SessionRequest session = new SessionRequest(world.engine, request);
        return route.handle(session);
    }

    public String GET(String uri, Map<String, String> params) {
        return (String) routing.GET(new MockRequestBuilder(uri, world.engine).withParams(params).withAdmin());
    }

    public Object POST(String uri, Map<String, String> params) {
        return routing.POST(new MockRequestBuilder(uri, world.engine).withParams(params).withAdmin());
    }

    public void assertRedirect(String uri) {
        Assert.assertEquals(this.redirectedUri, uri);
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
