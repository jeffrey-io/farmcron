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
            Field.STRING("subscription").makeIndex(false), // DONE
            Field.STRING("debug") // DONE
    );

    public static void link(final CounterCodeGen c) {
        c.section("Data: Subscriber");
    }

    public Subscriber() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

}
