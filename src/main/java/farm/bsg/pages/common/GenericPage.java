package farm.bsg.pages.common;

import farm.bsg.ProductEngine;
import farm.bsg.QueryEngine;

public class GenericPage {
    protected final ProductEngine  engine;
    protected final String       href;
    protected String             currentTitle;

    public GenericPage(ProductEngine engine, String href) {
        this.engine = engine;
        this.href = href;
    }
    
    public QueryEngine query() {
        return engine;
    }

}
