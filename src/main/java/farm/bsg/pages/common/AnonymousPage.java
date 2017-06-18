package farm.bsg.pages.common;

import farm.bsg.route.AnonymousRequest;

public class AnonymousPage extends GenericPage {
    private final AnonymousRequest request;
    public AnonymousPage(AnonymousRequest request, String href) {
        super(request.engine, href);
        this.request = request;
    }
}
