package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class CartItem extends RawObject {
    public CartItem() {
        super("cart-item/", //
                Field.STRING("cart").makeIndex(false), //
                Field.STRING("product").makeIndex(false), //
                Field.NUMBER("quantiy"),
                Field.STRING("customizations").makeIndex(false) //
                );
       
    }

    @Override
    protected void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: CartItem");
    }
    
}
