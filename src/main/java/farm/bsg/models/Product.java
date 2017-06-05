package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Product extends RawObject {
    public Product() {
        super("product/", //
                Field.STRING("name"), // -
                Field.STRING("description"), // -
                Field.STRING("category"), // -
                Field.STRING("customizations"), // -
                Field.NUMBER("price") //
                );
        
    }

    @Override
    protected void invalidateCache() {
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: Product");
    }

}
