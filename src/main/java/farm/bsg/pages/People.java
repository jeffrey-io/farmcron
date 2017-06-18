package farm.bsg.pages;

import java.util.UUID;

import farm.bsg.Security.Permission;
import farm.bsg.data.Type;
import farm.bsg.data.Value;
import farm.bsg.html.Html;
import farm.bsg.html.Input;
import farm.bsg.html.Table;
import farm.bsg.html.shit.ObjectModelForm;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class People extends SessionPage {
    public People(SessionRequest session) {
        super(session, "/people");
    }

    public String list() {
        StringBuilder sb = new StringBuilder();

        Table people = new Table("Name", "actions");
        sb.append("<h5>People</h5>");
        for (Value s : query().storage.scan("person/").values()) {
            Person person = new Person();
            person.injectValue(s);

            String actions = "";
            actions += "<a class=\"btn btn-primary\" href=\"/person-view?id=" + person.get("id") + "\">view</a>";
            actions += "<a class=\"btn btn-secondary\" href=\"/person-edit?id=" + person.get("id") + "\">edit</a>";
            people.row(person.get("name"), actions);
        }
        sb.append(people.toHtml());

        sb.append("<h5>Actions</h5><ul>");
        sb.append("<li><a class=\"btn btn-secondary\" href=\"/person-create\">Add Person</a></li>");
        sb.append("</ul>");
        return formalize_html(sb.toString());
    }

    public Person pullPerson() {
        Person person = query().person_by_id(session.getParam("id"), true);
        person.importValuesFromReqeust(session, "");
        return person;
    }

    public String create() {
        String username = session.getParam("username");
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Create New Person</h1>");
        sb.append("<form method=\"post\" action=\"/person-edit\">");
        sb.append(new Input("id").id_from_name().value(UUID.randomUUID().toString()));
        Table table = new Table(null, null);
        table.row(Html.label("login", "Login / username").toHtml(), //
                new Input("login").placeholder("sign in name...").clazz("form-control").text().id_from_name().autofocus().value(username).required());
        table.row(Html.label("new_password_1", "Password").toHtml(), //
                new Input("new_password_1").placeholder("new password...").clazz("form-control").password().id_from_name().autofocus().required());
        table.row(Html.label("new_password_2", "Confirm Password").toHtml(), //
                new Input("new_password_2").placeholder("new password confirm...").clazz("form-control").password().id_from_name().autofocus().required());
        sb.append(table.toString());
        sb.append("<input type=\"submit\">");
        sb.append("</form>");
        return sb.toString();
    }

    public String view() {
        Person person = pullPerson();
        StringBuilder sb = new StringBuilder();
        sb.append("<h5>Viewing: " + person.get("name") + "</h5>");

        Table table = new Table("Field", "Value", "Type");

        for (Type t : person.getTypes()) {
            String value = person.get(t.name());
            if (value == null) {
                value = "";
            }
            table.row(t.name(), value, t.type());
        }
        sb.append(table.toHtml());
        return formalize_html(sb.toString());
    }

    public String admin_edit() {
        Person person = pullPerson();

        String new_password_1 = session.getParam("new_password_1");
        String new_password_2 = session.getParam("new_password_2");
        if (new_password_1 != null) {
            if (new_password_1.equals(new_password_2)) {
                person.setPassword(new_password_1);
            } else {
                redirect("/person-create?username=" + person.get("login"));
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<h1>person edit</h1>");
        sb.append("<form method=\"post\" action=\"/commit-person-edit\">");
        sb.append(ObjectModelForm.htmlOf(person));
        sb.append("<hr /><input type=\"submit\">");
        sb.append("</form>");
        return formalize_html(sb.toString());
    }

    public Object commit() {
        Person person = pullPerson();
        if (session.hasNonNullQueryParam("_delete_")) {
            query().storage.put(person.getStorageKey(), null);
        } else {
            query().storage.put(person.getStorageKey(), new Value(person.toJson()));
        }
        redirect("/people");
        return null;
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/people", "People", Permission.SeePeopleTab);
        routing.get("/people", (session) -> new People(session).list());
        routing.get("/person-create", (session) -> new People(session).create());
        routing.get("/person-view", (session) -> new People(session).view());

        routing.get_or_post("/person-edit", (session) -> new People(session).admin_edit());
        routing.post("/commit-person-edit", (session) -> new People(session).commit());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: People");
    }

}
