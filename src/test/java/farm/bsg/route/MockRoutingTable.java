package farm.bsg.route;

import java.util.HashMap;

public class MockRoutingTable extends RoutingTable {

    private final HashMap<String, SessionRoute>   gets;
    private final HashMap<String, SessionRoute>   posts;
    private final HashMap<String, AnonymousRoute> public_get;
    private final HashMap<String, AnonymousRoute> public_post;
    private final HashMap<String, CustomerRoute>  customer_get;
    private final HashMap<String, CustomerRoute>  customer_post;
    private AnonymousRoute                        notFound = null;

    public MockRoutingTable() {
        this.gets = new HashMap<>();
        this.posts = new HashMap<>();
        this.public_get = new HashMap<>();
        this.public_post = new HashMap<>();
        this.customer_get = new HashMap<>();
        this.customer_post = new HashMap<>();
    }

    @Override
    public void get(ControlledURI path, SessionRoute route) {
        this.gets.put(path.toRoutingPattern(), route);
    }

    @Override
    public void post(ControlledURI path, SessionRoute route) {
        this.posts.put(path.toRoutingPattern(), route);
    }

    @Override
    public void setupTexting() {

    }

    @Override
    public void public_get(ControlledURI path, AnonymousRoute route) {
        public_get.put(path.toRoutingPattern(), route);
    }

    @Override
    public void public_post(ControlledURI path, AnonymousRoute route) {
        public_post.put(path.toRoutingPattern(), route);
    }

    @Override
    public void set_404(AnonymousRoute route) {
        notFound = route;
    }

    @Override
    public void customer_get(ControlledURI path, CustomerRoute route) {
        customer_get.put(path.toRoutingPattern(), route);
    }

    @Override
    public void customer_post(ControlledURI path, CustomerRoute route) {
        customer_post.put(path.toRoutingPattern(), route);
    }

    public Object GET(MockRequestBuilder builder) {
        if (gets.containsKey(builder.uri)) {
            return gets.get(builder.uri).handle(builder.asSessionRequest());
        }
        if (public_get.containsKey(builder.uri)) {
            return public_get.get(builder.uri).handle(builder.asAnonymousRequest());
        }
        if (customer_get.containsKey(builder.uri)) {
            return customer_get.get(builder.uri).handle(builder.asCustomerRequest());
        }
        if (notFound != null) {
            return notFound.handle(builder.asAnonymousRequest());
        }
        return null;
    }

    public Object POST(MockRequestBuilder builder) {
        if (posts.containsKey(builder.uri)) {
            return posts.get(builder.uri).handle(builder.asSessionRequest());
        }
        if (public_post.containsKey(builder.uri)) {
            return public_post.get(builder.uri).handle(builder.asAnonymousRequest());
        }
        if (customer_post.containsKey(builder.uri)) {
            return customer_post.get(builder.uri).handle(builder.asCustomerRequest());
        }
        return null;
    }

}
