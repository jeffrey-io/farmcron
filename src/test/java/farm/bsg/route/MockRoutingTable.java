package farm.bsg.route;

import java.util.HashMap;

public class MockRoutingTable extends RoutingTable {

    private final HashMap<String, SessionRoute> gets;
    private final HashMap<String, SessionRoute> posts;
    private final HashMap<String, AnonymousRoute> public_get;
    private final HashMap<String, AnonymousRoute> public_post;
    private AnonymousRoute notFound = null;
    
    public MockRoutingTable() {
        this.gets = new HashMap<>();
        this.posts = new HashMap<>();
        this.public_get = new HashMap<>();
        this.public_post = new HashMap<>();
    }

    @Override
    public void get(String path, SessionRoute route) {
        this.gets.put(path, route);
    }

    @Override
    public void post(String path, SessionRoute route) {
        this.posts.put(path, route);
    }

    @Override
    public void setupTexting() {
        
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
        notFound = route;
    }    
}
