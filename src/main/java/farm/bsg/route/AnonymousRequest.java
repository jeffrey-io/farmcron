package farm.bsg.route;

import farm.bsg.ProductEngine;

public class AnonymousRequest extends DelegateRequest {
    public final ProductEngine engine;

    /**
     *
     * @param engine
     *            the engine powering the data
     * @param delegate
     *            the request and response wrapper
     */
    public AnonymousRequest(final ProductEngine engine, final RequestResponseWrapper delegate) {
        super(delegate);
        this.engine = engine;
    }
}
