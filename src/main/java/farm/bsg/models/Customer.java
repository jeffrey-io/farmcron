package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Customer extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("customer/", //
            Field.STRING("email").makeIndex(true), // used
            Field.STRING("name"), // -
            Field.STRING("phone").makeIndex(false), // -

            Field.STRING("salt"), // used
            Field.STRING("hash"), // used

            Field.STRING("notification_token").makeIndex(false), // token to map SMS, Facebook
            Field.STRING("notification_uri"));

    public Customer() {
        super(SCHEMA);
    }

    @Override
    protected synchronized void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Customers");
    }
}
