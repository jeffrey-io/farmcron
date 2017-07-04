package farm.bsg.pages.common;

import farm.bsg.ProductEngine;
import farm.bsg.QueryEngine;
import farm.bsg.route.SimpleURI;

public class GenericPage {
    protected final ProductEngine engine;
    protected final String        href;
    protected String              currentTitle;

    public GenericPage(ProductEngine engine, SimpleURI uri) {
        this.engine = engine;
        this.href = uri.toRoutingPattern();
    }

    public QueryEngine query() {
        return engine;
    }

}
