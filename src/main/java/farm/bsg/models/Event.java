package farm.bsg.models;

import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;

public class Event extends RawObject {

    public static final ObjectSchema SCHEMA = new ObjectSchema("event/", //
            Field.STRING("name"), //
            Field.STRING("when") //
    );

    public Event() {
        super(SCHEMA);
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Event");
    }

    @Override
    protected void invalidateCache() {
    }

}
