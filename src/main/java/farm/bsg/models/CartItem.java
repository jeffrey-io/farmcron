package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class CartItem extends RawObject {
    
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("cart-item/", //
            Field.STRING("cart").makeIndex(false), //
            Field.STRING("product").makeIndex(false), //
            Field.NUMBER("quantity"),
            Field.STRING("customizations").makeIndex(false) //
            );

    public CartItem() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: CartItem");
    }
    
}
