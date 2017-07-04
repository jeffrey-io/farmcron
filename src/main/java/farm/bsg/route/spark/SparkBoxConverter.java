package farm.bsg.route.spark;

import farm.bsg.ProductEngine;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.AnonymousRoute;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.CustomerRoute;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SessionRoute;

public interface SparkBoxConverter<T, R> {
    public static final SparkBoxConverter<CustomerRequest, CustomerRoute>   TO_CUSTOMER_REQUEST  = (engine, request) -> new CustomerRequest(engine, request);

    public static final SparkBoxConverter<SessionRequest, SessionRoute>     TO_SESSION_REQUEST   = (engine, request) -> new SessionRequest(engine, request);

    public static final SparkBoxConverter<AnonymousRequest, AnonymousRoute> TO_ANONYMOUS_REQUEST = (engine, request) -> new AnonymousRequest(engine, request);

    public T convert(ProductEngine engine, SparkBox request);
}
