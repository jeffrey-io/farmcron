package farm.bsg.pages.common;

import farm.bsg.route.CustomerRequest;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.SimpleURI;

public class CustomerPage extends GenericPage {

    protected final CustomerRequest request;

    public CustomerPage(final CustomerRequest request, final SimpleURI uri) {
        super(request.engine, uri);
        this.request = request;
    }

    public void redirect(final FinishedHref href) {
        this.request.redirect(href);
    }
}
