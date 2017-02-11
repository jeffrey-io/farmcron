package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Subscription extends RawObject {
    public Subscription() {
        super("subscription/", //
                Field.STRING("name"), // USED

                Field.STRING("subscribe_keyword"), //
                Field.STRING("subscribe_message"), //

                Field.STRING("unsubscribe_keyword"), //
                Field.STRING("unsubscribe_message") //
        );
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Subscription");
    }
    
    @Override
    protected void invalidateCache() {
    }

}
