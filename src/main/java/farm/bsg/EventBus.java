package farm.bsg;

import org.slf4j.Logger;

import farm.bsg.models.SiteProperties;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.ops.Logs;

public class EventBus {
    private static final Logger LOG = Logs.of(EventBus.class);

    public static enum Event {
        TaskCreation("tc", "Task Creation", "Fires when a new task is created", true), // A new task was created

        TaskSummary("ts", "Task Summary", "Fires at the given subscription time", true), // a task summary of all tasks sent during morning (if there are any)

        DirectPublish("dp", "Direct Publish", "Fires when someone publishes", false);

        public final String  code;
        public final String  fullName;
        public final String  description;
        public final boolean automatic;

        private Event(String code, String fullName, String description, boolean automatic) {
            this.code = code;
            this.fullName = fullName;
            this.description = description;
            this.automatic = automatic;
        }

        public static Event fromCode(String code) {
            for (Event evt : Event.values()) {
                if (evt.code.equals(code)) {
                    return evt;
                }
            }
            return Event.DirectPublish;
        }
    }

    public static class EventPayload {
        public final String shortText;

        public EventPayload(String shortText) {
            this.shortText = shortText;
        }
    }

    private final QueryEngine query;

    public EventBus(QueryEngine query) {
        this.query = query;
    }

    public void trigger(Event event, EventPayload payload) {
        int count = 0;
        SiteProperties properties = query.siteproperties_get();
        for (Subscription sub : query.select_subscription().where_event_eq(event.code).done()) {
            for (Subscriber subscriber : query.select_subscriber().where_subscription_eq(sub.getId()).done()) {
                if (dispatch(subscriber, payload, properties)) {
                    count++;
                }
            }
        }
        LOG.info("sent:" + payload.shortText + " to:" + count);
    }

    public int publish(Subscription subscription, EventPayload payload) {
        int count = 0;
        SiteProperties properties = query.siteproperties_get();
        for (Subscriber subscriber : query.select_subscriber().where_subscription_eq(subscription.getId()).done()) {
            query.executor.execute(new Runnable() {
                @Override
                public void run() {
                    dispatch(subscriber, payload, properties);
                }
            });
            count++;
        }
        LOG.info("sent:" + payload.shortText + " to:" + count);
        return count;
    }

    public boolean dispatch(Subscriber subscriber, EventPayload payload, SiteProperties properties) {
        String source = subscriber.get("source");
        if ("SMS".equals(source)) {
            return properties.sendTextMessage(subscriber.get("from"), payload.shortText);
        }
        if ("EMAIL".equals(source)) {
            // TODO, need to set up email recv and email send.
        }
        if ("FB".equals(source)) {
            return properties.sendFacebookMessage(subscriber.get("from"), payload.shortText);
        }
        return false;
    }

}
