package farm.bsg.pages.common;

import farm.bsg.route.CustomerRequest;
import farm.bsg.route.SimpleURI;

public class CustomerPage extends GenericPage {

    protected final CustomerRequest request;

    public CustomerPage(CustomerRequest request, SimpleURI uri) {
        super(request.engine, uri);
        this.request = request;
    }

}
