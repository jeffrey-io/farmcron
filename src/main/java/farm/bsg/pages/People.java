package farm.bsg.pages;

import java.util.UUID;

import farm.bsg.Security.Permission;
import farm.bsg.data.Type;
import farm.bsg.data.Value;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class People extends SessionPage {
    public static SimpleURI PEOPLE             = new SimpleURI("/admin/people");

    public static SimpleURI PERSON_CREATE      = new SimpleURI("/admin/people;create");

    public static SimpleURI PERSON_VIEW        = new SimpleURI("/admin/people;view");

    public static SimpleURI PERSON_EDIT        = new SimpleURI("/admin/people;edit");

    public static SimpleURI PERSON_EDIT_COMMIT = new SimpleURI("/admin/people;edit;commit");

    public static void injectContact(final Block formInner, final Person person) {
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("phone", "Phone")) //
                .wrap(Html.input("phone").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("email", "E-mail")) //
                .wrap(Html.input("email").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("address_1", "Address")) //
                .wrap(Html.input("address_1").id_from_name().pull(person).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("address_2", "Address (optional)")) //
                .wrap(Html.input("address_2").id_from_name().pull(person).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("city", "City")) //
                .wrap(Html.input("city").id_from_name().pull(person).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("state", "State")) //
                .wrap(Html.input("state").id_from_name().pull(person).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("postal", "Postal")) //
                .wrap(Html.input("postal").id_from_name().pull(person).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("country", "Country")) //
                .wrap(Html.input("country").id_from_name().pull(person).text()));

    }

    public static void link(final CounterCodeGen c) {
        c.section("Page: People");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(PEOPLE, "People", Permission.PeopleManagement);
        routing.get(PEOPLE, (session) -> new People(session).list());
        routing.get(PERSON_CREATE, (session) -> new People(session).create());
        routing.get(PERSON_VIEW, (session) -> new People(session).view());

        routing.get_or_post(PERSON_EDIT, (session) -> new People(session).admin_edit());
        routing.post(PERSON_EDIT_COMMIT, (session) -> new People(session).commit());
    }

    public People(final SessionRequest session) {
        super(session, PEOPLE);
    }

    public String admin_edit() {
        person().mustHave(Permission.PeopleManagement);
        final Person person = pullPerson();

        final String new_password_1 = this.session.getParam("new_password_1");
        final String new_password_2 = this.session.getParam("new_password_2");
        if (new_password_1 != null) {
            if (new_password_1.equals(new_password_2)) {
                person.setPassword(new_password_1);
                query().put(person);
            } else {
                redirect(PERSON_CREATE.href("login", this.session.getParam("login"), "error", "password-not-match"));
            }
        }

        final Block page = Html.block();
        page.add(Html.W().h3().wrap("Edit Person"));

        final Block formInner = Html.block();
        formInner.add(Html.input("id").pull(person));

        formInner.add(Html.W().h3().wrap("Contact Information"));
        injectContact(formInner, person);

        formInner.add(Html.W().h3().wrap("Employment"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("default_mileage", "Default Mileage")) //
                .wrap(Html.input("default_mileage").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("hourly_wage_compesation", "Hourly Wage")) //
                .wrap(Html.input("hourly_wage_compesation").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("mileage_compensation", "Mileage Compenstation")) //
                .wrap(Html.input("mileage_compensation").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("fiscal_timezone", "Fiscal Timezone")) //
                .wrap(Html.input("fiscal_timezone").id_from_name().pull(person).text()));

        formInner.add(Html.W().h3().wrap("Bonus"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("bonus_target", "Bonus Target")) //
                .wrap(Html.input("bonus_target").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("min_performance_multiplier", "Minimum Performance Multiplier")) //
                .wrap(Html.input("min_performance_multiplier").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("max_performance_multiplier", "Maximum Performance Multiplier")) //
                .wrap(Html.input("max_performance_multiplier").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("ideal_weekly_hours", "Ideal Weekly Hours")) //
                .wrap(Html.input("ideal_weekly_hours").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("pto_earning_rate", "PTO Earning Rate")) //
                .wrap(Html.input("pto_earning_rate").id_from_name().pull(person).text()));

        formInner.add(Html.W().h3().wrap("Benefits"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("monthly_benefits", "Monthly Benefits")) //
                .wrap(Html.input("monthly_benefits").id_from_name().pull(person).text()));

        formInner.add(Html.W().h3().wrap("Tax Witholding"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("tax_withholding", "Tax Withholding")) //
                .wrap(Html.input("tax_withholding").id_from_name().pull(person).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Update").submit()));
        page.add(Html.form("post", PERSON_EDIT_COMMIT.href()).inner(formInner));
        return finish_pump(page);
    }

    public Object commit() {
        person().mustHave(Permission.PeopleManagement);
        final Person person = pullPerson();
        if (this.session.hasNonNullQueryParam("_delete_")) {
            query().storage.put(person.getStorageKey(), null);
        } else {
            query().storage.put(person.getStorageKey(), new Value(person.toJson()));
        }
        redirect(PEOPLE.href());
        return null;
    }

    public String create() {
        person().mustHave(Permission.PeopleManagement);
        final String login = this.session.getParam("login");

        final Block page = Html.block();
        page.add(Html.W().h3().wrap("Create New Person"));

        final Block formInner = Html.block();
        formInner.add(Html.input("id").value(UUID.randomUUID().toString()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("login", "Login")) //
                .wrap(Html.input("login").id_from_name().autofocus(login == null).value(login).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("new_password_1", "Password")) //
                .wrap(Html.input("new_password_1").id_from_name().password().autofocus(login != null).placeholder("new password...")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("new_password_2", "Confirm Password")) //
                .wrap(Html.input("new_password_2").id_from_name().password().placeholder("new password confirm...")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Create").submit()));
        page.add(Html.form("post", PERSON_EDIT.href()).inner(formInner));
        return finish_pump(page);
    }

    public String list() {
        person().mustHave(Permission.PeopleManagement);
        final Block page = Html.block();
        final Table people = new Table("Name", "actions");
        for (final Value s : query().storage.scan("person/").values()) {
            final Person person = new Person();
            person.injectValue(s);
            final Block actions = Html.block();
            actions.add(Html.link(PERSON_VIEW.href("id", person.getId()), "view").btn_secondary());
            actions.add(Html.link(PERSON_EDIT.href("id", person.getId()), "edit").btn_secondary());
            people.row(person.get("name"), actions);
        }
        page.add(Html.W().h3().wrap("People"));
        page.add(people);
        page.add(Html.W().h3().wrap("Actions"));
        page.add(Html.link(PERSON_CREATE.href(), "Create Person").btn_secondary());
        return finish_pump(page);
    }

    public Person pullPerson() {
        final Person person = query().person_by_id(this.session.getParam("id"), false);
        person.importValuesFromReqeust(this.session, "");
        return person;
    }

    public String view() {
        person().mustHave(Permission.PeopleManagement);
        final Person person = pullPerson();
        final Block page = Html.block();
        page.add(Html.W().h3().wrap("Viewing: " + person.get("name")));
        final Table table = new Table("Field", "Value", "Type");
        for (final Type t : person.getTypes()) {
            String value = person.get(t.name());
            if (value == null) {
                value = "";
            }
            table.row(t.name(), value, t.type());
        }
        page.add(table);
        return finish_pump(page);
    }

}
