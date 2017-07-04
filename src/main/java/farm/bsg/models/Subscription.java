package farm.bsg.models;

import farm.bsg.EventBus;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Subscription extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("subscription/", //
            Field.STRING("name"), // USED
            Field.STRING("description"), //

            Field.STRING("subscribe_keyword"), //
            Field.STRING("subscribe_message"), //

            Field.STRING("unsubscribe_keyword"), //
            Field.STRING("unsubscribe_message"), //

            Field.STRING("event").makeIndex(false) //
    );

    public static void link(final CounterCodeGen c) {
        c.section("Data: Subscription");
    }

    public Subscription() {
        super(SCHEMA);
    }

    public EventBus.Event getEvent() {
        return EventBus.Event.fromCode(get("event"));
    }

    @Override
    protected void invalidateCache() {
    }

    public boolean isInterest(final EventBus.Event event) {
        return event.code.equals(get("event"));
    }

    public boolean isOpen() {
        return !isNullOrEmpty("subscribe_keyword") && !isNullOrEmpty("unsubscribe_keyword");
    }

}
