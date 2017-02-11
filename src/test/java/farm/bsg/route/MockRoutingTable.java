package farm.bsg.route;

import java.util.HashMap;

public class MockRoutingTable extends RoutingTable {

    private final HashMap<String, SessionRoute> gets;
    private final HashMap<String, SessionRoute> posts;

    public MockRoutingTable() {
        this.gets = new HashMap<>();
        this.posts = new HashMap<>();
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
}
