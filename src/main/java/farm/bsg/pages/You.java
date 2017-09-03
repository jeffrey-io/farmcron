package farm.bsg.pages;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;

import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Input;
import farm.bsg.html.Table;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.ops.Logs;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class You extends SessionPage {
    private static final Logger LOG                     = Logs.of(You.class);

    public static SimpleURI     YOU                     = new SimpleURI("/admin/you");

    public static SimpleURI     YOU_CHANGE_PW           = new SimpleURI("/admin/you;change-password");

    public static SimpleURI     YOU_UPDATE_CONTACT_INFO = new SimpleURI("/admin/you;update-contact-info");

    public static SimpleURI     YOU_COMMIT_CONTACT_INFO = new SimpleURI("/admin/you;commit-contact-info");

    public static SimpleURI     GENERATE_DEVICE_TOKEN = new SimpleURI("/admin/you;generate-device-token");

    public static void link(final CounterCodeGen c) {
        c.section("Page: You");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(YOU, "You", Permission.Public);

        routing.text((engine, text) -> {
            final String message = text.message.toLowerCase().trim();
            if (message.startsWith("link")) {
                LOG.info("recieved link request from:" + text.from);
                final String token = message.substring(message.length() - 4).trim().toLowerCase();
                final Person person = engine.select_person().where_notification_token_eq(token).to_list().first();
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
        routing.get_or_post(YOU_CHANGE_PW, (session) -> new You(session).changepw());
        routing.get_or_post(YOU_UPDATE_CONTACT_INFO, (session) -> new You(session).edit_contact());
        routing.get_or_post(YOU_COMMIT_CONTACT_INFO, (session) -> new You(session).commit_contact());
        routing.get(GENERATE_DEVICE_TOKEN, (session) -> new You(session).generate_device_token());
    }

    public You(final SessionRequest session) {
        super(session, YOU);
        ensurePersonHasNotificationToken();
    }
    
    public String generate_device_token() {
        final Person person = person();
        byte[] token = new byte[10];
        ThreadLocalRandom.current().nextBytes(token);
        person.set("device_token", new String(Hex.encodeHex(token)));
        this.engine.put(person);
        person.sync(this.session.engine);
        redirect(YOU.href());
        return null;
    }

    public String changepw() {
        if (commitpw()) {
            redirect(YOU.href());
            return null;
        }
        final Block page = Html.block();
        page.add(Html.W().h5().wrap("Change Password"));
        final Table table = new Table(null, null);
        table.row(Html.label("old_password", "Current Password"), //
                new Input("old_password").placeholder("current password...").clazz("form-control").password().id_from_name().autofocus().required());

        table.row(Html.label("new_password_1", "Password"), //
                new Input("new_password_1").placeholder("new password...").clazz("form-control").password().id_from_name().autofocus().required());
        table.row(Html.label("new_password_2", "Confirm Password"), //
                new Input("new_password_2").placeholder("new password confirm...").clazz("form-control").password().id_from_name().autofocus().required());
        table.row("", Html.input("submit").id_from_name().value("Update").submit());
        page.add(Html.form("post", YOU_CHANGE_PW.href()).inner(table));
        return finish_pump(page);
    }

    public String commit_contact() {
        final Person person = person();
        if (query().projection_person_contact_info_of(this.session).apply(person).success()) {
            this.engine.put(person);
            person.sync(this.session.engine);
            redirect(YOU.href());
            return null;
        }
        return edit_contact();
    }

    public boolean commitpw() {
        final Person person = person();
        final String old_password = this.session.getParam("old_password");
        final String new_password_1 = this.session.getParam("new_password_1");
        final String new_password_2 = this.session.getParam("new_password_2");

        if (old_password == null || new_password_1 == null) {
            return false;
        }

        final boolean allowed = this.session.engine.auth.authenticateByUsernameAndPassword(person.login(), old_password).allowed;
        if (!allowed) {
            return false;
        }

        if (new_password_1.equals(new_password_2)) {
            person.setPassword(new_password_1);
            if (query().put(person).success()) {
                person.sync(this.session.engine);
                return true;
            }
            return false;
        }
        return false;
    }

    public String edit_contact() {
        final Person person = person();
        person.importValuesFromReqeust(this.session, "");
        final Block page = Html.block();
        page.add(Html.W().h3().wrap("Edit Contact Information"));

        final Block formInner = Html.block();

        page.add(Html.W().h3().wrap("Contact Information"));
        formInner.add(Html.input("id").pull(person));
        People.injectContact(formInner, person);
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Update").submit()));
        page.add(Html.form("post", YOU_COMMIT_CONTACT_INFO.href()).inner(formInner));
        return finish_pump(page);
    }

    private void ensurePersonHasNotificationToken() {
        final Person person = person();
        if (person.isNullOrEmpty("notification_token")) {
            final byte[] token = new byte[4];
            ThreadLocalRandom.current().nextBytes(token);
            person.set("notification_token", new String(Hex.encodeHex(token, true)));
            query().put(person);
        }
    }

    public String show() {
        final Block page = Html.block();
        final Person person = person();

        Table table = new Table("Field", "Value");
        table.row("login", person.get("login"));
        table.row("name", person.get("name"));
        table.row("e-mail", person.get("email"));
        table.row("phone", person.get("phone"));
        table.row("country", person.get("country"));
        table.row("time zone", person.get("fiscal_timezone"));
        page.add(Html.W().h5().wrap("Contact Information"));
        page.add(table);

        
        table = new Table("Field", "Value");
        table.row("Hourly Wage", person.getAsDouble("hourly_wage_compesation"));
        table.row("Monthly Benefits", person.getAsDouble("monthly_benefits"));
        table.row("Mileage Expense", person.getAsDouble("mileage_compensation"));
        table.row("Assumed Daily Mileage (mi)", person.getAsDouble("default_mileage"));
        table.row("Bonus Target", person.getAsDouble("bonus_target"));
        table.row("Minimum Performance Multipler for Bonus", person.getAsDouble("min_performance_multiplier"));
        table.row("Maximum Performance Multipler for Bonus", person.getAsDouble("max_performance_multiplier"));
        table.row("Ideal Weekly Hours", person.getAsDouble("ideal_weekly_hours"));
        table.row("PTO Earning Rate (hr/hr)", person.getAsDouble("pto_earning_rate"));

        page.add(Html.W().h5().wrap("Employment"));
        page.add(table);

        page.add(Html.W().h5().wrap("Notifications"));
        if (person.isNullOrEmpty("notification_uri")) {
            final String token = person.get("notification_token");
            page.add(Html.W().p().wrap("Currently, notifications are not set up for you. Text \"link " + token + "\" to the robot to link."));
        } else {
            final String uri = person.get("notification_uri");
            page.add(Html.W().p().wrap("Notifications are currently set up to use this token: \"" + uri + "\" If this is not working, then please clear the notification uri to start over."));
        }
        String deviceToken = person.get("device_token");
        if (deviceToken != null && !deviceToken.equals("")) {
            page.add(Html.W().h5().wrap("Device Token: " + deviceToken));
            page.add(Html.input("dt").text().value(engine.siteproperties_get().get("domain") + "::" + deviceToken));
        }

        page.add(Html.W().h5().wrap("Actions"));
        final Block actions = Html.block();
        actions.add(Html.W().li().wrap(Html.link(YOU_CHANGE_PW.href(), "Change Password").btn_secondary()));
        actions.add(Html.W().li().wrap(Html.link(YOU_UPDATE_CONTACT_INFO.href(), "Edit Contact Information").btn_secondary()));
        actions.add(Html.W().li().wrap(Html.link(GENERATE_DEVICE_TOKEN.href(), "Generate Device Token").btn_secondary()));
        page.add(Html.wrapped().ul().wrap(actions));

        return finish_pump(page);
    }
}
