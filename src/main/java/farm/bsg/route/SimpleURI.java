package farm.bsg.route;

import java.util.Map;

public class SimpleURI extends ControlledURI {

    private final String uri;
    
    public SimpleURI(String uri) {
        this.uri = uri;
    }
    
    @Override
    protected String href(Map<String, String> map) {
        return this.uri;
    }
}
