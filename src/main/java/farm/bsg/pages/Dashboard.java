package farm.bsg.pages;

import java.util.HashMap;

import farm.bsg.BsgCounters;
import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.models.PayrollEntry;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.Checks.W2;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Dashboard extends SessionPage {
    public static SimpleURI DASHBOARD = new SimpleURI("/admin/dashboard");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Dashboard");
        c.counter("dashboard_hits", "How many times a dashboard was viewed");
    }

    public static void link(final RoutingTable routing) throws Exception {
        routing.navbar(DASHBOARD, "Dashboard", Permission.Public);
        routing.get_or_post(DASHBOARD, (session) -> new Dashboard(session).show());
    }

    public Dashboard(final SessionRequest session) {
        super(session, DASHBOARD);
    }
    
    private double pto() {
        double pto = 0;
        for (PayrollEntry payroll : query().select_payrollentry().where_person_eq(session.getPerson().getId()).done()) {
            pto += payroll.getAsDouble("pto_change");
        }
        return pto;
    }

    public Object show() {
        BsgCounters.I.dashboard_hits.bump();
        final Block page = Html.block();
        
        page.add(Html.wrapped().h3().wrap("PTO Available:" + pto()));
        page.add(Html.wrapped().h5().wrap("Actions"));
        page.add(Html.wrapped().ul() //
                .wrap(Html.W().li().wrap(new Payroll(this.session).getReportPayrollLink(person()))) //
        );
        return finish_pump(page);
    }

}
