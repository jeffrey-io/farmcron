package farm.bsg.route;

import farm.bsg.ProductEngine;
import farm.bsg.models.Person;

public class ApiRequest extends DelegateRequest {
    public final ProductEngine engine;
    public final Person        person;

    /**
     * @param engine
     *            the engine powering the data
     * @param delegate
     *            the request and response wrapper
     */
    public ApiRequest(final ProductEngine engine, final RequestResponseWrapper delegate, final Person person) {
        super(delegate);
        this.engine = engine;
        this.person = person;
    }
}
