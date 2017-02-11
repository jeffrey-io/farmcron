package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.html.Table;
import farm.bsg.html.shit.ObjectModelForm;
import farm.bsg.models.Event;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class Events extends SessionPage {
    public Events(SessionRequest session) {
        super(session, "/events");
    }

    public static List<Event> getEvents(ProductEngine engine) {
        return engine.select_event().to_list().done();
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h5>All Events</h5>");
        Table table = new Table("Name", "When", "Actions");
        List<Event> events = Events.getEvents(session.engine);

        for (Event event : events) {
            String actions = "<a class=\"btn btn-secondary\" href=\"/event-edit?id=" + event.get("id") + "\">edit</a>";
            actions += "<a class=\"btn btn-secondary\" href=\"/event-view?id=" + event.get("id") + "\">view</a>";

            table.row(//
                    event.get("name"), //
                    event.get("when"), //
                    actions);
        }
        sb.append(table.toHtml());
        sb.append("<a class=\"btn btn-primary\" href=\"/new-event\">New Event</a>");
        return formalize_html(sb.toString());
    }

    public Event pullEvent() {
        Event event = query().event_by_id(session.getParam("id"), true);
        event.importValuesFromReqeust(session, "");
        return event;
    }

    public String view() {
        Event event = pullEvent();
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>view</h1>");
        sb.append(ObjectModelForm.htmlOf(event));
        sb.append("<h1>extended actions</h1>");
        return formalize_html(sb.toString());
    }

    public String edit() {
        Event event = pullEvent();
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>event edit</h1>");
        sb.append("<form method=\"post\" action=\"/commit-event-edit\">");
        sb.append(ObjectModelForm.htmlOf(event));
        sb.append("<hr /><input type=\"submit\">");
        sb.append("</form>");
        return formalize_html(sb.toString());
    }

    public String commit() {
        Event event = pullEvent();
        session.engine.save(event);
        redirect("/events");
        return null;
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/events", "Events", Permission.SeeEventsTab);

        routing.get("/events", (session) -> new Events(session).list());
        routing.get("/new-event", (session) -> {
            session.redirect("/event-edit?id=" + UUID.randomUUID().toString());
            return null;
        });
        routing.get("/event-view", (session) -> new Events(session).view());
        routing.get_or_post("/event-edit", (session) -> new Events(session).edit());
        routing.post("/commit-event-edit", (session) -> new Events(session).commit());

    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Events");
    }

}
