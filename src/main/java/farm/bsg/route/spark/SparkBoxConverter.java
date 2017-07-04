package farm.bsg.route.spark;

import farm.bsg.ProductEngine;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.AnonymousRoute;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.CustomerRoute;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SessionRoute;

public interface SparkBoxConverter<T, R> {
    public T convert(ProductEngine engine, SparkBox request);

    public static final SparkBoxConverter<CustomerRequest, CustomerRoute>   TO_CUSTOMER_REQUEST  = new SparkBoxConverter<CustomerRequest, CustomerRoute>() {
                                                                                                     @Override
                                                                                                     public CustomerRequest convert(ProductEngine engine, SparkBox request) {
                                                                                                         return new CustomerRequest(engine, request);
                                                                                                     }
                                                                                                 };

    public static final SparkBoxConverter<SessionRequest, SessionRoute>     TO_SESSION_REQUEST   = new SparkBoxConverter<SessionRequest, SessionRoute>() {
                                                                                                     @Override
                                                                                                     public SessionRequest convert(ProductEngine engine, SparkBox request) {
                                                                                                         return new SessionRequest(engine, request);
                                                                                                     }
                                                                                                 };

    public static final SparkBoxConverter<AnonymousRequest, AnonymousRoute> TO_ANONYMOUS_REQUEST = new SparkBoxConverter<AnonymousRequest, AnonymousRoute>() {
                                                                                                     @Override
                                                                                                     public AnonymousRequest convert(ProductEngine engine, SparkBox request) {
                                                                                                         return new AnonymousRequest(engine, request);
                                                                                                     }
                                                                                                 };
}
