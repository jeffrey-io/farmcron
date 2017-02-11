package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Subscriber extends RawObject {
    public Subscriber() {
        super("subscriber/", //
                Field.STRING("source"), // DONE
                Field.STRING("from"), // DONE
                Field.STRING("destination"), // DONE
                Field.STRING("subscription"), // DONE
                Field.STRING("debug") // DONE
        );
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: Subscriber");
    }
    
    @Override
    protected void invalidateCache() {
    }

}
