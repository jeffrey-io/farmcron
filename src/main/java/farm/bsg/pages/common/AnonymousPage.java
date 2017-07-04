package farm.bsg.pages.common;

import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.SimpleURI;

public class AnonymousPage extends GenericPage {
    protected final AnonymousRequest request;

    public AnonymousPage(final AnonymousRequest request, final SimpleURI uri) {
        super(request.engine, uri);
        this.request = request;
    }
}
