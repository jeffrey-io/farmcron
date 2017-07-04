package farm.bsg;

import org.slf4j.Logger;

import farm.bsg.models.SiteProperties;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.ops.Logs;

public class EventBus {
    public static enum Event {
        TaskCreation("tc", "Task Creation", "Fires when a new task is created", true), // A new task was created

        TaskSummary("ts", "Task Summary", "Fires at the given subscription time", true), // a task summary of all tasks sent during morning (if there are any)

        DirectPublish("dp", "Direct Publish", "Fires when someone publishes", false);

        public static Event fromCode(final String code) {
            for (final Event evt : Event.values()) {
                if (evt.code.equals(code)) {
                    return evt;
                }
            }
            return Event.DirectPublish;
        }

        public final String  code;
        public final String  fullName;
        public final String  description;

        public final boolean automatic;

        private Event(final String code, final String fullName, final String description, final boolean automatic) {
            this.code = code;
            this.fullName = fullName;
            this.description = description;
            this.automatic = automatic;
        }
    }

    public static class EventPayload {
        public final String shortText;

        public EventPayload(final String shortText) {
            this.shortText = shortText;
        }
    }

    private static final Logger LOG = Logs.of(EventBus.class);

    private final QueryEngine   query;

    public EventBus(final QueryEngine query) {
        this.query = query;
    }

    public boolean dispatch(final Subscriber subscriber, final EventPayload payload, final SiteProperties properties) {
        final String source = subscriber.get("source");
        if ("SMS".equals(source)) {
            return properties.sendTextMessage(subscriber.get("from"), payload.shortText);
        }
        if ("EMAIL".equals(source)) {
            // TODO, need to set up email recv and email send.
        }
        if ("facebook".equals(source)) {
            return properties.sendFacebookMessage(subscriber.get("from"), payload.shortText);
        }
        return false;
    }

    public int publish(final Subscription subscription, final EventPayload payload) {
        int count = 0;
        final SiteProperties properties = this.query.siteproperties_get();
        for (final Subscriber subscriber : this.query.select_subscriber().where_subscription_eq(subscription.getId()).done()) {
            this.query.executor.execute(() -> dispatch(subscriber, payload, properties));
            count++;
        }
        LOG.info("sent:" + payload.shortText + " to:" + count);
        return count;
    }

    public void trigger(final Event event, final EventPayload payload) {
        int count = 0;
        final SiteProperties properties = this.query.siteproperties_get();
        for (final Subscription sub : this.query.select_subscription().where_event_eq(event.code).done()) {
            for (final Subscriber subscriber : this.query.select_subscriber().where_subscription_eq(sub.getId()).done()) {
                if (dispatch(subscriber, payload, properties)) {
                    count++;
                }
            }
        }
        LOG.info("sent:" + payload.shortText + " to:" + count);
    }

}
