package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Cart extends RawObject {
    public Cart() {
        super("cart/", //
                Field.STRING("user").alwaysTrim().emptyStringSameAsNull().makeIndex(false) //
                );
       
    }

    @Override
    protected void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Cart");
    }
}
