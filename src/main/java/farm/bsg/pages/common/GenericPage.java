package farm.bsg.pages.common;

import com.github.jknack.handlebars.Handlebars;

import farm.bsg.ProductEngine;
import farm.bsg.QueryEngine;
import farm.bsg.route.SimpleURI;

public class GenericPage {
    public static final Handlebars compiler = new Handlebars();

    protected final ProductEngine  engine;
    protected final String         href;
    protected String               currentTitle;

    public GenericPage(final ProductEngine engine, final SimpleURI uri) {
        this.engine = engine;
        this.href = uri.toRoutingPattern();
    }

    public QueryEngine query() {
        return this.engine;
    }

}
