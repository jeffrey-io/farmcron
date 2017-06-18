package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.html.shit.ObjectModelForm;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.text.EnglishKeywordNormalizer;

public class Subscriptions extends SessionPage {
    public Subscriptions(SessionRequest session) {
        super(session, "/subscriptions");
    }

    public static List<Subscription> getSubscriptions(ProductEngine engine) {
        return engine.select_subscription().to_list().done();
    }

    public Subscription pullSubscription() {
        Subscription event = query().subscription_by_id(session.getParam("id"), true);
        event.importValuesFromReqeust(session, "");
        return event;
    }

    public static enum Action {
        Subscribe, Unsubscribe, None
    }

    public static class Result {
        public final Action       action;
        public final String       response;

        public final Subscription winner;

        public Result(Action action, String response, Subscription winner) {
            this.action = action;
            this.response = response;
            this.winner = winner;
        }
    }

    public static Result evaluate(ProductEngine engine, String text) {
        String normalizedText = EnglishKeywordNormalizer.normalize(text);
        for (Subscription subscription : getSubscriptions(engine)) {
            String sub = EnglishKeywordNormalizer.normalize(subscription.get("subscribe_keyword"));
            String unsub = EnglishKeywordNormalizer.normalize(subscription.get("unsubscribe_keyword"));
            if (normalizedText.equals(sub)) {
                return new Result(Action.Subscribe, subscription.get("subscribe_message"), subscription);
            }
            if (normalizedText.equals(unsub)) {
                return new Result(Action.Unsubscribe, subscription.get("unsubscribe_message"), subscription);
            }
        }
        return null;
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h5>All Subscriptions</h5>");
        Table table = new Table("Name", "Actions");
        List<Subscription> subs = Subscriptions.getSubscriptions(session.engine);
        for (Subscription sub : subs) {
            String actions = "<a class=\"btn btn-secondary\" href=\"/subscription-edit?id=" + sub.get("id") + "\">edit</a>";
            actions += "<a class=\"btn btn-secondary\" href=\"/subscription-view?id=" + sub.get("id") + "\">view</a>";

            table.row(//
                    sub.get("name"), //
                    actions);
        }
        sb.append(table.toHtml());
        sb.append("<a class=\"btn btn-primary\" href=\"/subscription-test\">Test Your Keywords</a>");
        sb.append("<a class=\"btn btn-secondary\" href=\"/new-subscription\">New Subscription</a>");
        return formalize_html(sb.toString());
    }

    public String test() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h5>Test Your Engine</h5>");
        sb.append("<form method=\"post\" action=\"/subscription-test\">");
        String message = session.getParam("message");
        sb.append(Html.label("message", "Message").toHtml());
        sb.append(Html.input("message").text().id_from_name().autofocus().value(message).toHtml());
        sb.append("<input type=\"submit\"></form>");
        if (message != null) {
            sb.append("<hr />");
            Result result = Subscriptions.evaluate(session.engine, message);

            if (result == null) {
                sb.append("<strong>The robot did not understand</strong>");
            } else {
                sb.append("<strong>Output:</strong>").append(result.response);
                sb.append("<br /> {action=" + result.action + "}");
                sb.append("<br /> {subscription=" + result.winner.get("name") + "}");
            }
        }
        return formalize_html(sb.toString());
    }

    public String view() {
        Subscription sub = pullSubscription();
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>view</h1>");
        sb.append(ObjectModelForm.htmlOf(sub));

        sb.append("<h1>subscribers</h1>");
        List<Subscriber> subscribers = query().select_subscriber().scope(sub.getId()).to_list().done();
        Table table = new Table("source", "from", "action");
        for (Subscriber subscriber : subscribers) {
            String actions = "<a class=\"btn btn-secondary\" href=\"/remove-subscriber?id=" + sub.getId() + "&from=" + subscriber.get("from") + "&source=" + subscriber.get("source") + "\">delete</a>";

            table.row( //
                    subscriber.get("source"), //
                    subscriber.get("from"), actions); //
        }
        sb.append(table.toHtml());

        sb.append("<form method=\"post\" action=\"/add-sms-subscriber\">");
        sb.append(Html.label("number", "Phone Number").toHtml());
        sb.append(Html.input("number").text().required().id_from_name().toHtml());

        sb.append(Html.input("id").value(sub.getId()).id_from_name().toHtml());

        sb.append("<input type=\"submit\" value=\"add\"></form>");

        return formalize_html(sb.toString());
    }

    public String remove() {
        String id = session.getParam("id");
        String source = session.getParam("source");
        String from = session.getParam("from");

        Subscriber subscriber = new Subscriber();
        subscriber.set("id", id + "/" + source + ";" + from);
        session.engine.storage.put(subscriber.getStorageKey(), null);
        redirect("/subscription-view?id=" + id);
        return null;
    }

    public String add_sms_subscriber() {
        String id = session.getParam("id");
        String number = session.getParam("number");
        if (number != null && id != null) {
            Subscriber subscriber = new Subscriber();
            subscriber.set("id", id + "/SMS;" + number);
            subscriber.set("from", number);
            subscriber.set("source", "SMS");
            subscriber.set("destination", "");
            subscriber.set("subscription", id);
            subscriber.set("debug", "{}");
            query().put(subscriber);
        }
        redirect("/subscription-view?id=" + id);
        return null;
    }

    public String edit() {
        Subscription sub = pullSubscription();
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>event edit</h1>");
        sb.append("<form method=\"post\" action=\"/commit-subscription-edit\">");
        sb.append(ObjectModelForm.htmlOf(sub));
        sb.append("<hr /><input type=\"submit\">");
        sb.append("</form>");
        return formalize_html(sb.toString());
    }

    public String commit() {
        Subscription sub = pullSubscription();
        query().put(sub);
        redirect("/subscriptions");
        return null;
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/subscriptions", "Subscriptions", Permission.SeeSubscripionsTab);

        routing.text((engine, message) -> {
            Result result = Subscriptions.evaluate(engine, message.message);
            // failed to route
            if (result == null) {
                return null;
            }

            String id = result.winner.getId() + "/" + message.source + ";" + message.from;
            if (result.action == Action.Subscribe) {
                Subscriber sub = new Subscriber();
                sub.set("id", id);
                sub.set("from", message.from);
                sub.set("source", message.source);
                sub.set("destination", message.to);
                sub.set("subscription", result.winner.getId());
                sub.set("debug", message.debug);
                engine.put(sub);
            }
            if (result.action == Action.Unsubscribe) {
                Subscriber sub = engine.subscriber_by_id(id, false);
                if (sub != null) {
                  engine.del(sub);
                }
            }
            return message.generateResponse(result.response);
        });

        routing.get("/subscriptions", (session) -> new Subscriptions(session).list());

        routing.post("/add-sms-subscriber", (session) -> new Subscriptions(session).add_sms_subscriber());
        routing.get_or_post("/remove-subscriber", (session) -> new Subscriptions(session).remove());

        routing.get("/new-subscription", (session) -> {
            session.redirect("/subscription-edit?id=" + UUID.randomUUID().toString());
            return null;
        });
        routing.get_or_post("/subscription-edit", (session) -> new Subscriptions(session).edit());
        routing.get_or_post("/subscription-test", (session) -> new Subscriptions(session).test());
        routing.get("/subscription-view", (session) -> new Subscriptions(session).view());
        routing.post("/commit-subscription-edit", (session) -> new Subscriptions(session).commit());
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Page: Subscriptions");
    }

}
