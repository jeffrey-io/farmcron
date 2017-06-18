package farm.bsg.pages;

import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;

import farm.bsg.Security.Permission;
import farm.bsg.html.Html;
import farm.bsg.html.Input;
import farm.bsg.html.Table;
import farm.bsg.html.shit.ObjectModelForm;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.ops.Logs;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class You extends SessionPage {
    private static final Logger LOG = Logs.of(You.class);

    public You(SessionRequest session) {
        super(session, YOU);
    }

    public String show() {
        StringBuilder sb = new StringBuilder();
        Person person = person();

        Table table = new Table("Field", "Value");
        table.row("login", person.get("login"));
        table.row("name", person.get("name"));
        table.row("e-mail", person.get("email"));
        table.row("phone", person.get("phone"));
        table.row("country", person.get("country"));
        table.row("time zone", person.get("fiscal_timezone"));
        sb.append("<h5>Contact Information</h5>");
        sb.append(table.toHtml());

        table = new Table("Field", "Value");
        table.row("Equipment Skills", person.get("equipment_skills"));
        sb.append("<h5>Abilities</h5>");
        sb.append(table.toHtml());

        table = new Table("Field", "Value");
        table.row("Hourly Wage", person.getAsDouble("hourly_wage_compesation"));
        table.row("Monthly Benefits", person.getAsDouble("monthly_benefits"));
        table.row("Mileage Expense", person.getAsDouble("mileage_compensation"));
        table.row("Assumed Daily Mileage (mi)", person.getAsDouble("default_mileage"));
        table.row("Bonus Target", person.getAsDouble("bonus_target"));
        table.row("Minimum Performance Multipler for Bonus", person.getAsDouble("min_performance_multiplier"));
        table.row("Maximum Performance Multipler for Bonus", person.getAsDouble("max_performance_multiplier"));
        sb.append("<h5>Employment</h5>");
        sb.append(table.toHtml());

        sb.append("<h5>Actions</h5>");
        sb.append("<li><a class=\"btn btn-secondary\" href=\"/change-password\">Change Password</a></li>");
        String superCookie = person.get("super_cookie");
        if (superCookie != null) {
            superCookie.trim();
            if (superCookie.length() == 0) {
                superCookie = null;
            }
        }

        sb.append("<li><a class=\"btn btn-secondary\" href=\"/make-super-cookie\">Invalidate Super Cookie</a></li>");
        if (superCookie != null) {
            sb.append("<li><a class=\"btn btn-secondary\" href=\"/kill-super-cookie\">Kill Super Cookie</a></li>");
            sb.append("<li><a class=\"btn btn-secondary\" href=\"/dashboard?_sc=" + person.get("super_cookie") + "\">Use Super Cookie on Dashboard</a></li>");
        }
        // sb.append("<li><a class=\"btn btn-secondary\" href=\"/edit-you\">Edit Contact Information</a></li>");
        return formalize_html(sb.toString());
    }

    public Object mutate_notification(boolean kill) {
        Person person = pullPerson();
        if (kill) {
            person.set("notification_token", null);
        } else {
            byte[] token = new byte[3];
            ThreadLocalRandom.current().nextBytes(token);
            person.set("notification_token", Hex.encodeHexString(token).toLowerCase());
        }
        redirect("/you");
        query().put(person);
        return null;
    }

    public Object mutate_super_cookie(boolean kill) {
        Person person = pullPerson();
        if (kill) {
            person.set("super_cookie", null);
        } else {
            person.set("super_cookie", makeSuperCookie(person));
        }
        query().put(person);
        redirect("/you");
        return null;
    }

    public Person pullPerson() {
        Person person = person();
        person.importValuesFromReqeust(session, "");
        return person;
    }

    public String edit() {
        Person person = pullPerson();

        StringBuilder sb = new StringBuilder();
        sb.append("<h1>person edit</h1>");
        sb.append("<form method=\"post\" action=\"/commit-person-edit\">");
        sb.append(ObjectModelForm.htmlOf(person));
        sb.append("<hr /><input type=\"submit\">");
        sb.append("</form>");

        return formalize_html(sb.toString());
    }

    public String changepw() {
        if (commitpw()) {
            redirect("/you");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<h5>Change Password</h5>");
        sb.append("<form method=\"post\" action=\"/change-password\">");
        Table table = new Table(null, null);
        table.row(Html.label("old_password", "Current Password"), //
                new Input("old_password").placeholder("current password...").clazz("form-control").password().id_from_name().autofocus().required());

        table.row(Html.label("new_password_1", "Password"), //
                new Input("new_password_1").placeholder("new password...").clazz("form-control").password().id_from_name().autofocus().required());
        table.row(Html.label("new_password_2", "Confirm Password"), //
                new Input("new_password_2").placeholder("new password confirm...").clazz("form-control").password().id_from_name().autofocus().required());
        sb.append(table.toString());
        sb.append("<input type=\"submit\">");
        sb.append("</form>");
        return formalize_html(sb.toString());
    }

    public boolean commitpw() {
        Person person = pullPerson();
        String old_password = session.getParam("old_password");
        String new_password_1 = session.getParam("new_password_1");
        String new_password_2 = session.getParam("new_password_2");

        if (old_password == null || new_password_1 == null) {
            return false;
        }

        boolean allowed = session.engine.auth.authenticateByUsernameAndPassword(person.login(), old_password).allowed;
        if (!allowed) {
            return false;
        }

        if (new_password_1.equals(new_password_2)) {
            person.setPassword(new_password_1);
            if (query().put(person).success()) {
                person.sync(session.engine);
                return true;
            }
            return false;
        }
        return false;
    }

    private String makeSuperCookie(Person person) {
        try {
            String cookie1 = session.engine.auth.generateCookie();
            String cookie2 = session.engine.auth.generateCookie();
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(cookie1.getBytes());
            digest.update(person.get("salt").getBytes());
            digest.update(person.get("hash").getBytes());
            digest.update(person.get("login").getBytes());
            digest.update(cookie2.getBytes());
            String middle = Hex.encodeHexString(digest.digest());
            return cookie1 + middle + cookie2;
        } catch (Exception err) {
            return null;
        }
    }

    public static void link(RoutingTable routing) {
        routing.navbar(YOU, "You", Permission.Public);

        routing.text((engine, text) -> {
            String message = text.message.toLowerCase().trim();
            if (message.startsWith("link")) {
                LOG.info("recieved link request from:" + text.from);
                String token = message.substring(message.length() - 4);
                Person person = engine.select_person().where_notification_token_eq(token).to_list().first();
                if (person != null) {
                    LOG.info("recieved request associated!");
                    person.set("notification_uri", text.getNotificationUri());
                    if (engine.put(person).success()) {
                        return text.generateResponse("Linked");
                    } else {
                        return text.generateResponse("Failed to write");
                    }
                } else {
                    LOG.info("was unable to find notification token: " + token);
                    return text.generateResponse("Failed to find notification token");
                }
            }

            return null;
        });
        routing.get(YOU, (session) -> new You(session).show());
        routing.get_or_post(YOU_EDIT, (session) -> new You(session).edit());
        routing.get_or_post(YOU_CHANGE_PW, (session) -> new You(session).changepw());

        routing.get_or_post(YOU_MAKE_SC, (session) -> new You(session).mutate_super_cookie(false));
        routing.get_or_post(YOU_KILL_SC, (session) -> new You(session).mutate_super_cookie(true));
    }
    
    public static SimpleURI YOU = new SimpleURI("/you");
    public static SimpleURI YOU_EDIT = new SimpleURI("/edit-you");
    public static SimpleURI YOU_CHANGE_PW = new SimpleURI("/change-password");
    public static SimpleURI YOU_MAKE_SC = new SimpleURI("/make-super-cookie");
    public static SimpleURI YOU_KILL_SC = new SimpleURI("/kill-super-cookie");

    public static void link(CounterCodeGen c) {
        c.section("Page: You");
    }

}
