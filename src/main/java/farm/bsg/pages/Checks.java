package farm.bsg.pages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.binary.Hex;

import farm.bsg.Security.Permission;
import farm.bsg.data.RawObject;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Baton;
import farm.bsg.models.Check;
import farm.bsg.models.PayrollEntry;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Checks extends SessionPage {
    public static final SimpleURI CHECKS_HOME       = new SimpleURI("/admin/checks");

    public static final SimpleURI CHECKS_TAXES      = new SimpleURI("/admin/checks;taxes");

    public static final SimpleURI CHECKS_BONUS      = new SimpleURI("/admin/checks;bonus");
    public static final SimpleURI CHECKS_GIVE_BONUS = new SimpleURI("/admin/checks;give_bonus");

    public static final SimpleURI CHECKS_TAXES_PAID = new SimpleURI("/admin/checks;taxes-paid");

    public static final SimpleURI CHECKS_AUDIT      = new SimpleURI("/admin/checks;audit");

    public static final SimpleURI CHECKS_CONFIRM    = new SimpleURI("/admin/checks;confirm");

    public static final SimpleURI CHECKS_VIEW       = new SimpleURI("/admin/checks;view");

    public static final SimpleURI CHECKS_W2         = new SimpleURI("/admin/checks;w2");
    public static final SimpleURI CHECKS_PTO         = new SimpleURI("/admin/checks;pto");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Checks");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(CHECKS_HOME, "Checks", Permission.CheckWriter);
        routing.get(CHECKS_HOME, (session) -> new Checks(session).show());
        routing.get(CHECKS_TAXES, (session) -> new Checks(session).taxes());

        routing.get(CHECKS_BONUS, (session) -> new Checks(session).bonus());
        routing.get(CHECKS_GIVE_BONUS, (session) -> new Checks(session).bonus_grant());

        routing.get(CHECKS_TAXES_PAID, (session) -> new Checks(session).indicateTaxsDone());
        routing.get(CHECKS_AUDIT, (session) -> new Checks(session).audit());
        routing.get(CHECKS_CONFIRM, (session) -> new Checks(session).confirm());
        routing.get(CHECKS_VIEW, (session) -> new Checks(session).visualize());
        routing.get(CHECKS_W2, (session) -> new Checks(session).w2());
        routing.get(CHECKS_PTO, (session) -> new Checks(session).pto());
    }

    public Checks(final SessionRequest session) {
        super(session, CHECKS_HOME);
    }

    public String audit() {
        person().mustHave(Permission.CheckWriter);
        final Person person = query().person_by_id(this.session.getParam("employee"), false);
        final Block page = Html.block();
        final List<PayrollEntry> entries = query().select_payrollentry() //
                .where_unpaid_eq(person.getId()).to_list() //
                .inline_order_lexographically_asc_by("reported").done();
        page.add(Html.wrapped().h5().wrap("Audit:").wrap(person.login()));
        page.add(Payroll.payrollTable(entries, false, true));
        double owed = 0.0;
        for (final PayrollEntry payrollEntry : entries) {
            owed += payrollEntry.getAsDouble("owed");
        }
        final int checksum = ((int) Math.round(owed * 100) * 10009 + entries.size() * 7) % 109859;
        page.add(Html.link(CHECKS_CONFIRM.href("employee", person.getId(), "checksum", Integer.toString(checksum)), "Confirm").btn_primary());
        return finish_pump(page);
    }

    public String bonus() {
        person().mustHave(Permission.CheckWriter);
        final Block page = Html.block();
        final String currentPeriod = Check.fiscalQuarterFromFiscalDay(person().getCurrentDay());
        page.add(Html.wrapped().h5().wrap("Available Bonuses"));
        final Table table = computeBonusAvail(currentPeriod);
        page.add(table);
        return finish_pump(page);
    }

    public String bonus_grant() {
        final String id = this.session.getParam("id");

        // dumb a baton (need to rename this...)
        final Baton baton = new Baton();
        baton.set("id", id);
        query().put(baton);

        final Check check = new Check();
        check.set("id", this.session.getParam("person") + "_bonus_" + this.session.getParam("quarter"));
        check.set("ref", this.session.getParam("quarter") + "BONUS");
        check.set("person", this.session.getParam("person"));
        check.set("generated", RawObject.isoTimestamp());
        check.set("fiscal_day", person().getCurrentDay());
        check.set("ready", "yes");
        check.set("checksum", "-1");
        check.set("bonus_related", true);
        check.set("payment", this.session.getParam("bonus"));
        check.set("pto_change", "0");
        query().put(check);

        redirect(CHECKS_VIEW.href("id", check.getId()));
        return null;
    }

    public Table computeBonusAvail(final String currentPeriod) {
        final String[] ratingLabels = new String[] { "bad", "meh", "good", "great", "fantastic" };
        final Table table = Html.table("Person", "Quarter", "Value", "Actions");
        for (final Person person : query().select_person().done()) {
            final HashMap<String, ArrayList<Check>> checksByQuater = query().select_check().where_person_eq(person.getId()).to_list().groupBy((c) -> {
                return c.getFiscalQuarter();
            });
            final TreeSet<String> domain = new TreeSet<>(checksByQuater.keySet());
            for (final String quarter : domain) {
                final boolean ready = quarter.compareTo(currentPeriod) < 0;
                if (ready) {
                    double sum = 0;
                    for (final Check check : checksByQuater.get(quarter)) {
                        if (!check.getAsBoolean("bonus_related")) {
                            sum += check.getAsDouble("payment");
                        }
                    }
                    final String batonId = person.getId() + ";GRANTBONUS;" + quarter;
                    final Baton baton = query().baton_by_id(batonId, false);
                    if (baton == null && sum >= 0.01) {
                        final double mn = person.getAsDouble("min_performance_multiplier") * person.getAsDouble("bonus_target") * sum;
                        final double mx = person.getAsDouble("max_performance_multiplier") * person.getAsDouble("bonus_target") * sum;

                        final double[] ratings = new double[] { mn, mn * 0.7 + mx * 0.3, mn * 0.5 + mx * 0.5, mn * 0.3 + mx * 0.7, mx };
                        final Block action = Html.block();
                        for (int k = 0; k < ratingLabels.length; k++) {
                            action.add(Html.link(CHECKS_GIVE_BONUS.href("id", batonId, "quarter", quarter, "person", person.getId(), "rating", ratingLabels[k], "bonus", Double.toString(ratings[k])), "Bonus: " + ratingLabels[k] + " (" + ratings[k] + ")").btn_primary());
                        }
                        table.row(person.get("name"), quarter, sum, action);
                    }
                }
            }
        }
        return table;
    }

    public Table computeTaxTable(final String currentPeriod) {
        final Table table = Html.table("Person", "Event", "Value", "Action");
        for (final Person person : query().select_person().done()) {
            final HashMap<String, ArrayList<Check>> checksByQuater = query().select_check().where_person_eq(person.getId()).to_list().groupBy((c) -> {
                return c.getFiscalQuarter();
            });
            final TreeSet<String> domain = new TreeSet<>(checksByQuater.keySet());
            for (final String quarter : domain) {
                final boolean ready = quarter.compareTo(currentPeriod) < 0;
                if (ready) {
                    double sum = 0;
                    for (final Check check : checksByQuater.get(quarter)) {
                        sum += check.getAsDouble("payment");
                    }
                    final String batonId = person.getId() + ";UNEMPLOYMENT;" + quarter;
                    final Baton baton = query().baton_by_id(batonId, false);

                    if (baton == null) {
                        final HtmlPump action = Html.link(CHECKS_TAXES_PAID.href("id", batonId), "Indicate Paid").btn_primary();
                        table.row(person.get("name"), "Unemployment Taxes:" + quarter, sum, action);
                    }
                }
            }
        }
        return table;
    }

    public String confirm() {
        person().mustHave(Permission.CheckWriter);
        final Person employee = query().person_by_id(this.session.getParam("employee"), false);
        if (employee == null) {
            return null;
        }
        double owed = 0.0;
        final List<PayrollEntry> entries = PayrollEntry.getUnpaidEntries(query(), employee.getId());
        double pto_change = 0;
        for (final PayrollEntry payrollEntry : entries) {
            owed += payrollEntry.getAsDouble("owed");
            pto_change += payrollEntry.getAsDouble("pto_change");
        }
        final int checksum = ((int) Math.round(owed * 100) * 10009 + entries.size() * 7) % 109859;
        final int expected = Integer.parseInt(this.session.getParam("checksum"));

        if (checksum == expected) {
            // create the check, so it exists
            final Check check = makeCheck(employee, checksum);
            query().put(check);

            // associate the entries to the check
            for (final PayrollEntry payrollEntry : entries) {
                payrollEntry.set("check", check.getId());
                payrollEntry.set("unpaid", "not_" + check.getId());
                query().put(payrollEntry);
            }

            // commit the check to being ready
            check.set("ready", "yes");
            check.set("payment", owed);
            check.set("pto_change", pto_change);
            query().put(check);

            // populate the check
            redirect(CHECKS_VIEW.href("id", check.getId()));
            return null;
        } else {
            redirect(CHECKS_AUDIT.href("employee", employee.getId(), "error", "newdata"));
            return null;
        }
    }

    private HtmlPump fragmentChecksPaidWithin() {
        person().mustHave(Permission.CheckWriter);
        final List<Check> checks = query().select_check().where_ready_eq("yes").to_list().inline_order_lexographically_desc_by("generated").limit(50).done();
        if (checks.size() == 0) {
            return null;
        }
        final Table table = Table.start("Quarter", "Date", "Payment", "PTO+/-", "Action");
        for (final Check check : checks) {
            final Link action = Html.link(CHECKS_VIEW.href("id", check.getId()), "View").btn_success();
            final String day = check.get("fiscal_day");
            table.row(check.getFiscalQuarter(), day, check.get("payment"), check.get("pto_change"), action);
        }

        final Block fragment = Html.block();
        fragment.add(Html.W().h5().wrap("Checks Written"));
        fragment.add(table);
        return fragment;
    }

    private HtmlPump fragmentOutstandingBalancesForAllEmployees() {
        person().mustHave(Permission.CheckWriter);

        final Set<String> unpaidEmployees = query().get_payrollentry_unpaid_index_keys();
        if (unpaidEmployees.size() == 0) {
            return null;
        }

        final Block fragment = Html.block();
        fragment.add(Html.wrapped().h5().wrap("Outstanding Balances"));
        final Table table = new Table("employee", "owed", "pto+/-", "actions");
        for (final String unpaidEmployee : unpaidEmployees) {
            final Person person = query().person_by_id(unpaidEmployee, false);
            if (person == null) {
                continue;
            }
            String name = person.get("name");
            if (name == null) {
                name = person.login();
            }
            double owed = 0.0;
            double pto = 0.0;
            for (final PayrollEntry value : PayrollEntry.getUnpaidEntries(query(), unpaidEmployee)) {
                owed += value.getAsDouble("owed");
                pto += value.getAsDouble("pto_change");
            }
            final HtmlPump action = Html.link(CHECKS_AUDIT.href("employee", unpaidEmployee), "Pay").btn_success();
            table.row(person.login(), owed, pto, action);
        }
        fragment.add(table);
        return fragment;
    }

    private String generateCheckId(final int checksum) {
        final byte[] ref = new byte[5];
        ThreadLocalRandom.current().nextBytes(ref);
        ref[0] = (byte) checksum;
        ref[4] = (byte) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return Hex.encodeHexString(ref).toUpperCase();
    }

    public String indicateTaxsDone() {
        final String id = this.session.getParam("id");
        final Baton baton = new Baton();
        baton.set("id", id);
        query().put(baton);
        redirect(CHECKS_TAXES.href());
        return null;
    }

    private Check makeCheck(final Person person, final int checksum) {
        person().mustHave(Permission.CheckWriter);
        final String checkId = generateCheckId(checksum);
        final Check check = new Check();
        check.generateAndSetId();
        check.set("person", person.getId());
        check.set("ref", checkId);
        check.set("checksum", checksum);
        check.set("ready", "no");
        check.set("generated", RawObject.isoTimestamp());
        check.set("fiscal_day", person.getCurrentDay());
        return check;
    }

    public String show() {
        person().mustHave(Permission.CheckWriter);
        final Block page = Html.block();
        page.add(Html.link(CHECKS_TAXES.href(), "Check Tax Status").btn_success());
        page.add(Html.link(CHECKS_BONUS.href(), "Grant Bonuses").btn_success());
        page.add(Html.link(CHECKS_W2.href(), "W2 By Year").btn_success());
        page.add(Html.link(CHECKS_PTO.href(), "PTO By Person").btn_success());
        page.add(fragmentOutstandingBalancesForAllEmployees());
        page.add(fragmentChecksPaidWithin());
        return finish_pump(page);
    }

    public String taxes() {
        person().mustHave(Permission.CheckWriter);
        final Block page = Html.block();
        final String currentPeriod = Check.fiscalQuarterFromFiscalDay(person().getCurrentDay());
        page.add(Html.wrapped().h5().wrap("Taxes"));
        final Table table = computeTaxTable(currentPeriod);
        page.add(table);
        return finish_pump(page);
    }

    public String visualize() {
        person().mustHave(Permission.CheckWriter);
        final String checkId = this.session.getParam("id");
        final Check check = query().check_by_id(checkId, false);
        if (check == null) {
            return null;
        }

        final Person employee = query().person_by_id(check.get("person"), false);
        if (employee == null) {
            return null;
        }

        final Block page = Html.block();

        page.add(Html.wrapped().h5().wrap("How to Write the Check"));
        final Table table = Html.table("Field", "Value");
        table.row("To", employee.get("name"));
        table.row("Amount", check.get("payment"));
        table.row("For", check.get("ref"));
        table.row("Was Properly Commited", check.get("ready"));
        table.row("Bonus", check.getAsBoolean("bonus_related"));
        page.add(table);

        page.add(Html.wrapped().h5().wrap("Payroll from Check"));
        final List<PayrollEntry> entries = query().select_payrollentry().where_unpaid_eq("not_" + checkId).done();
        page.add(Payroll.payrollTable(entries, false, true));

        return finish_pump(page);
    }

    public static class W2 {
        public final Person person;
        public final String year;
        public double       paid;
        public double       travel;
        public double       benefits;
        public double       withheld;
        public double       miles;

        public W2(Person person, String year) {
            this.person = person;
            this.year = year;
            this.paid = 0;
            this.travel = 0;
            this.benefits = 0;
            this.withheld = 0;
            this.miles = 0;
                    
        }

        public void add(PayrollEntry payroll) {
            this.paid += payroll.getAsDouble("owed");
            this.withheld += payroll.getAsDouble("taxes");
            this.travel += payroll.getAsDouble("mileage") * payroll.getAsDouble("mileage_compensation");
            this.benefits += payroll.getAsDouble("benefits");
            this.miles += payroll.getAsDouble("mileage");
        }
        
        public void add(Check check) {
            this.paid += check.getAsDouble("payment");
        }
    }

    public String pto() {
        person().mustHave(Permission.CheckWriter);
        final Block page = Html.block();
        page.add(Html.wrapped().h5().wrap("PTO Balance"));
        final Table table = Html.table("Employee", "PTO Balance");

        HashMap<String, Double> entries = new HashMap<>();

        for (PayrollEntry payroll : query().select_payrollentry().done()) {
            String key = payroll.get("person");
            Double pto = entries.get(key);
            if (pto == null) {
                pto = 0d;
            }
            entries.put(key, pto + payroll.getAsDouble("pto_change"));
        }
        
        for (Entry<String, Double> entry : entries.entrySet()) {
            Person person =  query().person_by_id(entry.getKey(), false);
            table.row(person.get("name"), entry.getValue());
        }

        page.add(table);
        return finish_pump(page);

    }

    public String w2() {
        person().mustHave(Permission.CheckWriter);

        final Block page = Html.block();

        page.add(Html.wrapped().h5().wrap("All W2"));
        final Table table = Html.table("Employee", "Year", "Paid", "Travel", "Miles", "Benefits", "Withheld");

        HashMap<String, W2> entries = new HashMap<>();

        for (PayrollEntry payroll : query().select_payrollentry().done()) {
            String year = payroll.get("fiscal_day").substring(0, 4);
            String key = payroll.get("person") + ";" + year;
            W2 w2 = entries.get(key);
            if (w2 == null) {
                w2 = new W2(query().person_by_id(payroll.get("person"), false), year);
                entries.put(key, w2);
            }
            w2.add(payroll);
        }
        
        for (Check check : query().select_check().done()) {
            if (check.getAsBoolean("bonus_related")) {
                String year = check.get("fiscal_day").substring(0, 4);
                String key = check.get("person") + ";" + year;
                W2 w2 = entries.get(key);
                if (w2 == null) {
                    w2 = new W2(query().person_by_id(check.get("person"), false), year);
                    entries.put(key, w2);
                }
                w2.add(check);
                
            }
        }

        for (Entry<String, W2> entry : entries.entrySet()) {
            W2 w2 = entry.getValue();
            table.row(entry.getValue().person.get("name"), w2.year, w2.paid, w2.travel, w2.miles, w2.benefits, w2.withheld);
        }

        page.add(table);
        return finish_pump(page);
    }
}
