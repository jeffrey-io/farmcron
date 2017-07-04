package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Cart extends RawObject {

    public static ObjectSchema SCHEMA = ObjectSchema.persisted("cart/", //
            Field.STRING("customer").alwaysTrim().emptyStringSameAsNull().makeIndex(false), //
            Field.STRING("task").makeIndex(false), //
            Field.STRING("state") // "" --> "wait" (waiting for task) --> "fulfilled" (fulfilled)
    );

    public static void link(final CounterCodeGen c) {
        c.section("Data: Cart");
    }

    public Cart() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

}
