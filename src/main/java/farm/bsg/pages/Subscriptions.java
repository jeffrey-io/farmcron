package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

import farm.bsg.EventBus.Event;
import farm.bsg.EventBus.EventPayload;
import farm.bsg.QueryEngine;
import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Table;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;
import farm.bsg.route.text.EnglishKeywordNormalizer;

public class Subscriptions extends SessionPage {
    public static enum Action {
        Subscribe, Unsubscribe, None
    }

    public static class Result {
        public final Action       action;
        public final String       response;

        public final Subscription winner;

        public Result(final Action action, final String response, final Subscription winner) {
            this.action = action;
            this.response = response;
            this.winner = winner;
        }
    }

    public static SimpleURI SUBSCRIPTIONS                  = new SimpleURI("/admin/subscriptions");

    public static SimpleURI SUBSCRIPTIONS_NEW              = new SimpleURI("/admin/subscriptions;create");

    public static SimpleURI SUBSCRIPTIONS_EDIT             = new SimpleURI("/admin/subscriptions;edit");

    public static SimpleURI SUBSCRIPTIONS_VIEW             = new SimpleURI("/admin/subscriptions;view");

    public static SimpleURI SUBSCRIPTIONS_ADD_SUBSCRIBER   = new SimpleURI("/admin/subscriptions;subscriber;add");

    public static SimpleURI SUBSCRIPTIONS_REMOVE_SUBCRIBER = new SimpleURI("/admin/subscriptions;subscriber;remove");

    public static SimpleURI SUBSCRIPTIONS_PUBLISH          = new SimpleURI("/admin/subscriptions;publish");

    public static SimpleURI SUBSCRIPTIONS_TEST             = new SimpleURI("/admin/subscriptions;test");

    public static SimpleURI SUBSCRIPTIONS_COMMIT_EDIT      = new SimpleURI("/admin/subscriptions;commit");

    public static Result evaluate(final QueryEngine engine, final String text) {
        final String normalizedText = EnglishKeywordNormalizer.normalize(text);
        for (final Subscription subscription : getSubscriptions(engine)) {
            final String sub = EnglishKeywordNormalizer.normalize(subscription.get("subscribe_keyword"));
            final String unsub = EnglishKeywordNormalizer.normalize(subscription.get("unsubscribe_keyword"));
            if (normalizedText.equals(sub)) {
                return new Result(Action.Subscribe, subscription.get("subscribe_message"), subscription);
            }
            if (normalizedText.equals(unsub)) {
                return new Result(Action.Unsubscribe, subscription.get("unsubscribe_message"), subscription);
            }
        }
        return null;
    }

    public static List<Subscription> getSubscriptions(final QueryEngine engine) {
        return engine.select_subscription().to_list().done();
    }

    public static void link(final CounterCodeGen c) {
        c.section("Page: Subscriptions");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(SUBSCRIPTIONS, "Subscriptions", Permission.SubscriptionView);

        routing.text((engine, message) -> {
            final Result result = Subscriptions.evaluate(engine, message.message);
            // failed to route
            if (result == null) {
                return null;
            }

            final String id = result.winner.getId() + "/" + message.source + ";" + message.from;
            if (result.action == Action.Subscribe) {
                final Subscriber sub = new Subscriber();
                sub.set("id", id);
                sub.set("from", message.from);
                sub.set("source", message.source);
                sub.set("destination", message.to);
                sub.set("subscription", result.winner.getId());
                sub.set("debug", message.debug);
                engine.put(sub);
            }
            if (result.action == Action.Unsubscribe) {
                final Subscriber sub = engine.subscriber_by_id(id, false);
                if (sub != null) {
                    engine.del(sub);
                }
            }
            return message.generateResponse(result.response);
        });

        routing.get(SUBSCRIPTIONS, (session) -> new Subscriptions(session).list());
        routing.post(SUBSCRIPTIONS_ADD_SUBSCRIBER, (session) -> new Subscriptions(session).add());
        routing.get_or_post(SUBSCRIPTIONS_REMOVE_SUBCRIBER, (session) -> new Subscriptions(session).remove());
        routing.post(SUBSCRIPTIONS_PUBLISH, (session) -> new Subscriptions(session).publish());

        routing.get(SUBSCRIPTIONS_NEW, (session) -> {
            session.redirect(SUBSCRIPTIONS_EDIT.href("id", UUID.randomUUID().toString()));
            return null;
        });
        routing.get_or_post(SUBSCRIPTIONS_EDIT, (session) -> new Subscriptions(session).edit());
        routing.get_or_post(SUBSCRIPTIONS_TEST, (session) -> new Subscriptions(session).test());
        routing.get(SUBSCRIPTIONS_VIEW, (session) -> new Subscriptions(session).view());
        routing.post(SUBSCRIPTIONS_COMMIT_EDIT, (session) -> new Subscriptions(session).commit());
    }

    public Subscriptions(final SessionRequest session) {
        super(session, SUBSCRIPTIONS);
    }

    public String add() {
        person().mustHave(Permission.SubscriptionView);
        person().mustHave(Permission.SubscriptionWrite);
        final Subscriber subscriber = new Subscriber();
        subscriber.set("id", getConstructedId());
        subscriber.importValuesFromReqeust(this.session, "");
        query().put(subscriber);
        redirect(SUBSCRIPTIONS_VIEW.href("id", this.session.getParam("subscription")));
        return null;
    }

    public String commit() {
        person().mustHave(Permission.SubscriptionView);
        person().mustHave(Permission.SubscriptionWrite);
        final Subscription sub = pullSubscription();
        query().put(sub);
        redirect(SUBSCRIPTIONS.href());
        return null;
    }

    private String edit() {
        person().mustHave(Permission.SubscriptionView);
        person().mustHave(Permission.SubscriptionWrite);
        final Subscription sub = pullSubscription();
        final Block formInner = Html.block();
        formInner.add(Html.input("id").pull(sub));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(sub).text()).wrap(Html.wrapped().small().muted_form_text().wrap("The name of the subscription.")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(sub).textarea(4, 50)).wrap(Html.wrapped().small().muted_form_text().wrap("The description of the subscription.")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("subscribe_keyword", "Subscribe Keyword")) //
                .wrap(Html.input("subscribe_keyword").id_from_name().pull(sub).text()).wrap(Html.wrapped().small().muted_form_text().wrap(".")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("subscribe_message", "Subscribe Message")) //
                .wrap(Html.input("subscribe_message").id_from_name().pull(sub).textarea(4, 50)).wrap(Html.wrapped().small().muted_form_text().wrap("The message sent on a subscribe.")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("unsubscribe_keyword", "Unsubscribe Keyword")) //
                .wrap(Html.input("unsubscribe_keyword").id_from_name().pull(sub).text()).wrap(Html.wrapped().small().muted_form_text().wrap(".")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("unsubscribe_message", "Unsubscribe Message")) //
                .wrap(Html.input("unsubscribe_message").id_from_name().pull(sub).textarea(4, 50)).wrap(Html.wrapped().small().muted_form_text().wrap("The message sent on an unsubscribe.")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("event", "Event")) //
                .wrap(Html.input("event").id_from_name().pull(sub).select("", "tc", "d")).wrap(Html.wrapped().small().muted_form_text().wrap("The message sent on an unsubscribe.")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Save").submit()));

        final Block page = Html.block();
        page.add(Html.wrapped().h4().wrap("Editing of Subscription"));
        page.add(Html.form("post", SUBSCRIPTIONS_COMMIT_EDIT.href()).inner(formInner));
        return finish_pump(page);
    }

    private String getConstructedId() {
        final String subscription = this.session.getParam("subscription");
        String source = this.session.getParam("source");
        String from = this.session.getParam("from");
        if ("".equals(from)) {
            from = null;
        }
        if ("".equals(source)) {
            source = null;
        }
        return subscription + "/" + source + ";" + from;
    }

    public String list() {
        person().mustHave(Permission.SubscriptionView);
        final Block page = Html.block();
        page.add(Html.wrapped().h3().wrap("All Subscriptions"));

        final Table table = new Table("Name", "Actions");
        final List<Subscription> subs = Subscriptions.getSubscriptions(query());
        for (final Subscription sub : subs) {
            final HtmlPump actions = Html.block() //
                    .add_if(person().has(Permission.SubscriptionWrite), Html.link(SUBSCRIPTIONS_EDIT.href("id", sub.getId()), "edit").btn_secondary()) //
                    .add(Html.link(SUBSCRIPTIONS_VIEW.href("id", sub.getId()), "view").btn_secondary());
            table.row(//
                    sub.get("name"), //
                    actions);
        }
        page.add(table);
        page.add_if(person().has(Permission.SubscriptionWrite), Html.link(SUBSCRIPTIONS_NEW.href(), "Create New Subscription").btn_primary());
        page.add(Html.link(SUBSCRIPTIONS_TEST.href(), "Test Robot").btn_secondary());
        return finish_pump(page);
    }

    public String publish() {
        person().mustHave(Permission.SubscriptionView);
        person().mustHave(Permission.SubscriptionPublish);
        final Subscription sub = query().subscription_by_id(this.session.getParam("subscription"), false);
        if (sub == null) {
            return "invalid sub";
        }
        final String shortMessage = this.session.getParam("short_message");
        if ("".equals(shortMessage) || shortMessage == null) {
            return "no publish payload";
        }
        final EventPayload payload = new EventPayload(shortMessage);
        final int count = this.engine.eventBus.publish(sub, payload);
        return "Published:" + count;
    }

    public Subscription pullSubscription() {
        final Subscription event = query().subscription_by_id(this.session.getParam("id"), true);
        event.importValuesFromReqeust(this.session, "");
        return event;
    }

    public String remove() {
        person().mustHave(Permission.SubscriptionView);
        person().mustHave(Permission.SubscriptionWrite);

        final Subscriber subscriber = query().subscriber_by_id(getConstructedId(), false);
        if (subscriber != null) {
            query().del(subscriber);
        }
        redirect(SUBSCRIPTIONS_VIEW.href("id", this.session.getParam("subscription")));
        return null;
    }

    public String test() {
        person().mustHave(Permission.SubscriptionView);
        final Block page = Html.block();
        page.add(Html.wrapped().h3().wrap("Test Robot"));

        final Block formInner = Html.block();
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("message", "message")) //
                .wrap(Html.input("message").id_from_name().text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Test").submit()));
        page.add(Html.form("post", SUBSCRIPTIONS_TEST.href()).inner(formInner));

        final String message = this.session.getParam("message");
        if (message != null) {
            final Result result = Subscriptions.evaluate(query(), message);

            if (result == null) {
                page.add(Html.wrapped().strong().wrap("The robot did not understand."));
            } else {
                page.add(Html.wrapped().strong().wrap("Output: " + result.action + " ; subscription=" + result.winner.get("name")));
            }
        }
        return finish_pump(page);
    }

    public String view() {
        person().mustHave(Permission.SubscriptionView);
        final Subscription sub = pullSubscription();

        final Block page = Html.block();
        page.add(Html.wrapped().h2().wrap(sub.get("name")));

        page.add(Html.wrapped().h4().wrap("Acquisition"));
        if (sub.isOpen()) {
            page.add(Html.W().p().wrap("This subscription is open to the public via the robot."));
            final Table commands = new Table("Robot Command", "Robot Response");
            commands.row(sub.get("subscribe_keyword"), sub.get("subscribe_message"));
            commands.row(sub.get("unsubscribe_keyword"), sub.get("unsubscribe_message"));
            page.add(commands);
        } else {
            page.add(Html.W().p().wrap("This subscription is not open to public via the robot, and only site admins can add subscribers."));
        }

        page.add(Html.wrapped().h4().wrap("Trigger"));
        final Event event = sub.getEvent();
        if (event.automatic) {
            page.add(Html.block().add("This subscription is ").add(Html.W().strong().wrap("automatic")).add(". It will fire on '").add(event.name()).add("' events; which means it will fire when ").add(event.description));
        } else {
            page.add(Html.block().add("This subscription is ").add(Html.W().strong().wrap("manual")).add(". It will fire only when a site admin publishes"));

            final Block formInner = Html.block();
            formInner.add(Html.input("subscription").value(sub.getId()));
            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("short_message", "Short Message (SMS, Facebook)")) //
                    .wrap(Html.input("short_message").id_from_name().text()));
            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Publish").submit()));
            page.add(Html.form("post", SUBSCRIPTIONS_PUBLISH.href()).inner(formInner));
        }

        page.add(Html.wrapped().h4().wrap("Subscribers"));
        final List<Subscriber> subscribers = query().select_subscriber().scope(sub.getId()).to_list().done();
        if (subscribers.size() > 0) {
            final Table table = new Table("source", "from", "action");
            for (final Subscriber subscriber : subscribers) {
                final String href = SUBSCRIPTIONS_REMOVE_SUBCRIBER.href("subscription", sub.getId(), "from", subscriber.get("from"), "source", subscriber.get("source")).value;
                final String actions = "<a class=\"btn btn-secondary\" href=\"" + href + "\">delete</a>";
                table.row( //
                        subscriber.get("source"), //
                        subscriber.get("from"), actions); //
            }
            page.add(table);
        }

        if (person().has(Permission.SubscriptionWrite)) {
            final Block formInner = Html.block();
            formInner.add(Html.input("subscription").value(sub.getId()));
            formInner.add(Html.input("source").value("SMS"));
            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("from", "Phone Number")) //
                    .wrap(Html.input("from").id_from_name().text()));
            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Update").submit()));
            page.add(Html.wrapped().h4().wrap("Add SMS Subscriber"));
            page.add(Html.form("post", SUBSCRIPTIONS_ADD_SUBSCRIBER.href()).inner(formInner));
        }
        return finish_pump(page);
    }

}
