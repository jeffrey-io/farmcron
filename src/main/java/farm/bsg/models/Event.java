package farm.bsg.models;

import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.data.Field;

public class Event extends RawObject {
    public Event() {
        super("event/", //
                Field.STRING("name"), //
                Field.STRING("when") //
        );
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Event");
    }

    @Override
    protected void invalidateCache() {
    }

}
