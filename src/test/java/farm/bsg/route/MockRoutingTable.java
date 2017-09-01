package farm.bsg.route;

import java.util.HashMap;

public class MockRoutingTable extends RoutingTable {

    private final HashMap<String, SessionRoute>   gets;
    private final HashMap<String, SessionRoute>   posts;
    private final HashMap<String, AnonymousRoute> public_get;
    private final HashMap<String, AnonymousRoute> public_post;
    private final HashMap<String, CustomerRoute>  customer_get;
    private final HashMap<String, CustomerRoute>  customer_post;
    private final HashMap<String, ApiAction>  api_port;
    private AnonymousRoute                        notFound = null;

    public MockRoutingTable() {
        this.gets = new HashMap<>();
        this.posts = new HashMap<>();
        this.public_get = new HashMap<>();
        this.public_post = new HashMap<>();
        this.customer_get = new HashMap<>();
        this.customer_post = new HashMap<>();
        this.api_port = new HashMap<>();
    }

    @Override
    public void customer_get(final ControlledURI path, final CustomerRoute route) {
        this.customer_get.put(path.toRoutingPattern(), route);
    }

    @Override
    public void customer_post(final ControlledURI path, final CustomerRoute route) {
        this.customer_post.put(path.toRoutingPattern(), route);
    }

    @Override
    public void get(final ControlledURI path, final SessionRoute route) {
        this.gets.put(path.toRoutingPattern(), route);
    }

    public Object GET(final MockRequestBuilder builder) {
        if (this.gets.containsKey(builder.uri)) {
            return this.gets.get(builder.uri).handle(builder.asSessionRequest());
        }
        if (this.public_get.containsKey(builder.uri)) {
            return this.public_get.get(builder.uri).handle(builder.asAnonymousRequest());
        }
        if (this.customer_get.containsKey(builder.uri)) {
            return this.customer_get.get(builder.uri).handle(builder.asCustomerRequest());
        }
        if (this.notFound != null) {
            return this.notFound.handle(builder.asAnonymousRequest());
        }
        return null;
    }

    @Override
    public void post(final ControlledURI path, final SessionRoute route) {
        this.posts.put(path.toRoutingPattern(), route);
    }

    public Object POST(final MockRequestBuilder builder) {
        if (this.posts.containsKey(builder.uri)) {
            return this.posts.get(builder.uri).handle(builder.asSessionRequest());
        }
        if (this.public_post.containsKey(builder.uri)) {
            return this.public_post.get(builder.uri).handle(builder.asAnonymousRequest());
        }
        if (this.customer_post.containsKey(builder.uri)) {
            return this.customer_post.get(builder.uri).handle(builder.asCustomerRequest());
        }
        return null;
    }

    @Override
    public void public_get(final ControlledURI path, final AnonymousRoute route) {
        this.public_get.put(path.toRoutingPattern(), route);
    }

    @Override
    public void public_post(final ControlledURI path, final AnonymousRoute route) {
        this.public_post.put(path.toRoutingPattern(), route);
    }

    @Override
    public void set_404(final AnonymousRoute route) {
        this.notFound = route;
    }

    @Override
    public void setupTexting() {

    }
    
    @Override
    public void api_post(ControlledURI path, ApiAction route) {
        api_port.put(path.toRoutingPattern(),  route);
    }

}
