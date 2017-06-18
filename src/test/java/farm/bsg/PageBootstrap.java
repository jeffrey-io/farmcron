package farm.bsg;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import farm.bsg.data.Authenticator.AuthResult;
import farm.bsg.route.AnonymousRoute;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RequestResponseWrapper;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SessionRoute;

public class PageBootstrap {

    public final TestWorld                        world;
    public final MultiTenantRouter                router;
    private final HashMap<String, SessionRoute>   get;
    private final HashMap<String, SessionRoute>   post;
    private final HashMap<String, AnonymousRoute> public_get;
    private final HashMap<String, AnonymousRoute> public_post;
    private AnonymousRoute notFoundRoute;
    private final RoutingTable                    routing;

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

    public Object get_object(String uri, Map<String, String> params) {
        return execute(uri, get.get(uri), params);
    }

    public Object post_object(String uri, Map<String, String> params) {
        return execute(uri, post.get(uri), params);
    }

    public String GET(String uri, Map<String, String> params) {
        String html = (String) execute(uri, get.get(uri), params);
        return html;
    }

    public Object POST(String uri, Map<String, String> params) {
        String html = (String) execute(uri, post.get(uri), params);
        return html;
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
        this.get = new HashMap<>();
        this.post = new HashMap<>();
        this.public_get = new HashMap<>();
        this.public_post = new HashMap<>();
        this.routing = new RoutingTable() {
            @Override
            public void setupTexting() {
                // meh, for later
            }

            @Override
            public void post(String path, SessionRoute route) {
                post.put(path, route);
            }

            @Override
            public void get(String path, SessionRoute route) {
                get.put(path, route);
            }

            @Override
            public void public_get(String path, AnonymousRoute route) {
                public_get.put(path, route);
            }

            @Override
            public void public_post(String path, AnonymousRoute route) {
                public_post.put(path, route);
            }

            @Override
            public void set_404(AnonymousRoute route) {
                notFoundRoute = route;
            }
        };
        Linker.link(this.routing, this.router);
    }
}
