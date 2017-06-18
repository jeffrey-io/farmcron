package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Subscriber extends RawObject {
    
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("subscriber/", //
            Field.STRING("source"), // DONE
            Field.STRING("from"), // DONE
            Field.STRING("destination"), // DONE
            Field.STRING("subscription"), // DONE
            Field.STRING("debug") // DONE
    );
            
    public Subscriber() {
        super(SCHEMA);
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: Subscriber");
    }
    
    @Override
    protected void invalidateCache() {
    }

}
