package farm.bsg.pages;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.binary.Hex;
import farm.bsg.Security.Permission;
import farm.bsg.data.RawObject;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Check;
import farm.bsg.models.PayrollEntry;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class Checks extends SessionPage {
    public Checks(SessionRequest session) {
        super(session, "/checks");
    }

    private HtmlPump fragmentOutstandingBalancesForAllEmployees() {
        if (!has(Permission.SeeOutstandingChecksForEveryone)) {
            return null;
        }

        Set<String> unpaidEmployees = query().get_payrollentry_unpaid_index_keys();
        if (unpaidEmployees.size() == 0) {
            return null;
        }

        Block fragment = Html.block();
        fragment.add(Html.wrapped().h5().wrap("Outstanding Balances"));
        Table table = new Table("employee", "owed", "actions");
        for (String unpaidEmployee : unpaidEmployees) {
            Person person = query().person_by_id(unpaidEmployee, false);
            if (person == null) {
                continue;
            }
            String name = person.get("name");
            if (name == null) {
                name = person.login();
            }
            double owed = 0.0;
            for (PayrollEntry value : PayrollEntry.getUnpaidEntries(session.engine, unpaidEmployee)) {
                owed += value.getAsDouble("owed");
            }
            HtmlPump action = null;
            if (has(Permission.CheckMake)) {
                action = Html.link("/audit-check?employee=" + unpaidEmployee, "Pay").btn_success();
            }
            table.row(person.login(), owed, action);
        }
        fragment.add(table);
        return fragment;
    }

    private HtmlPump fragmentChecksPaidWithin() {
        List<Check> checks = query().select_check().where_ready_eq("yes").to_list().inline_order_lexographically_desc_by("generated").limit(50).done();
        if (checks.size() == 0) {
            return null;
        }
        Table table = Table.start("Date", "Payment", "Action");
        for (Check check : checks) {
            Link action = Html.link("/check-view?id=" + check.getId(), "View").btn_success();
            table.row(check.get("fiscal_day"), check.get("payment"), action);
        }

        Block fragment = Html.block();
        fragment.add(Html.W().h5().wrap("Checks Written"));
        fragment.add(table);
        return fragment;
    }

    public String show() {
        Block page = Html.block();
        page.add(fragmentOutstandingBalancesForAllEmployees());
        page.add(fragmentChecksPaidWithin());
        return finish_pump(page);
    }

    public String audit() {
        Person person = query().person_by_id(session.getParam("employee"), false);
        Block page = Html.block();
        List<PayrollEntry> entries = query().select_payrollentry() //
                .where_unpaid_eq(person.getId()).to_list() //
                .inline_order_lexographically_asc_by("reported").done();
        page.add(Html.wrapped().h5().wrap("Audit:").wrap(person.login()));
        page.add(Payroll.payrollTable(entries, false, true));
        double owed = 0.0;
        for (PayrollEntry payrollEntry : entries) {
            owed += payrollEntry.getAsDouble("owed");
        }
        int checksum = ((int) Math.round(owed * 100) * 10009 + entries.size() * 7) % 109859;
        String href = "/confirm-check?employee=" + person.getId() + "&checksum=" + checksum;
        page.add(Html.link(href, "Confirm").btn_primary());
        return finish_pump(page);
    }

    private String generateCheckId(int checksum) {
        byte[] ref = new byte[5];
        ThreadLocalRandom.current().nextBytes(ref);
        ref[0] = (byte) checksum;
        ref[4] = (byte) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return Hex.encodeHexString(ref).toUpperCase();
    }

    private Check makeCheck(Person person, int checksum) {
        String checkId = generateCheckId(checksum);
        Check check = new Check();
        check.generateAndSetId();
        check.set("person", person.getId());
        check.set("ref", checkId);
        check.set("checksum", checksum);
        check.set("ready", "no");
        check.set("generated", RawObject.isoTimestamp());
        check.set("fiscal_day", person.getCurrentDay());
        return check;
    }

    public String visualize() {
        String checkId = session.getParam("id");
        Check check = query().check_by_id(checkId, false);
        if (check == null) {
            return null;
        }

        Person employee = query().person_by_id(check.get("person"), false);
        if (employee == null) {
            return null;
        }

        Block page = Html.block();

        page.add(Html.wrapped().h5().wrap("How to Write the Check"));
        Table table = Html.table("Field", "Value");
        table.row("To", employee.get("name"));
        table.row("Amount", check.get("payment"));
        table.row("For", check.get("ref"));
        table.row("Was Properly Commited", check.get("ready"));
        page.add(table);

        page.add(Html.wrapped().h5().wrap("Payroll from Check"));
        List<PayrollEntry> entries = query().select_payrollentry().where_unpaid_eq("not_" + checkId).done();
        page.add(Payroll.payrollTable(entries, false, true));

        return finish_pump(page);
    }

    public String confirm() {
        Person employee = query().person_by_id(session.getParam("employee"), false);
        if (employee == null) {
            return null;
        }
        double owed = 0.0;
        List<PayrollEntry> entries = PayrollEntry.getUnpaidEntries(session.engine, employee.getId());
        for (PayrollEntry payrollEntry : entries) {
            owed += payrollEntry.getAsDouble("owed");
        }
        int checksum = ((int) Math.round(owed * 100) * 10009 + entries.size() * 7) % 109859;
        int expected = Integer.parseInt(session.getParam("checksum"));

        if (checksum == expected) {
            // create the check, so it exists
            Check check = makeCheck(employee, checksum);
            session.engine.put(check);

            // associate the entries to the check
            for (PayrollEntry payrollEntry : entries) {
                payrollEntry.set("check", check.getId());
                payrollEntry.set("unpaid", "not_" + check.getId());
                session.engine.put(payrollEntry);
            }

            // commit the check to being ready
            check.set("ready", "yes");
            check.set("payment", owed);
            query().put(check);

            // populate the check
            redirect("/check-view?id=" + check.getId());
            return null;
        } else {
            redirect("/audit-check?employee=" + employee.getId() + "&error=newdata");
            return null;
        }
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/checks", "Checks", Permission.SeeChecksTab);
        routing.get("/checks", (session) -> new Checks(session).show());
        routing.get("/audit-check", (session) -> new Checks(session).audit());
        routing.get("/confirm-check", (session) -> new Checks(session).confirm());
        routing.get("/check-view", (session) -> new Checks(session).visualize());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Checks");
    }
}
