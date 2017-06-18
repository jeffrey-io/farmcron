package farm.bsg.route;

import farm.bsg.ProductEngine;

public class AnonymousRequest extends DelegateRequest  {
    public final ProductEngine engine;

    /**
     * 
     * @param engine
     *            the engine powering the data
     * @param req
     *            the spark request
     * @param res
     *            the spark response
     */
    public AnonymousRequest(ProductEngine engine, RequestResponseWrapper delegate) {
        super(delegate);
        this.engine = engine;
    }
}
