package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Cart extends RawObject {

    public static ObjectSchema SCHEMA = new ObjectSchema("cart/", //
            Field.STRING("user").alwaysTrim().emptyStringSameAsNull().makeIndex(false) //
    );

    public Cart() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Cart");
    }
}
