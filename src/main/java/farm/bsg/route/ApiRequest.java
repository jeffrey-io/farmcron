package farm.bsg.route;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> getIdsFromCommaEncodedParam(final String key) {
        final ArrayList<String> ids = new ArrayList<>();
        String value = getParam(key);
        if (value == null) {
            return ids;
        }
        value = value.trim();
        if ("".equals(value)) {
            return ids;
        }
        for (final String id : value.split(",")) {
            ids.add(id);
        }
        return ids;
    }
}
