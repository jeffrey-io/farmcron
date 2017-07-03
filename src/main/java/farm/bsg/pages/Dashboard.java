package farm.bsg.pages;

import farm.bsg.BsgCounters;
import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Dashboard extends SessionPage {
    public Dashboard(SessionRequest session) {
        super(session, DASHBOARD);
    }

    public Object show() {
        BsgCounters.I.dashboard_hits.bump();
        Block page = Html.block();
        if (has(Permission.HabitsUnlocked)) {
            page.add(Html.wrapped().h5().wrap("Habit Briefing"));
            page.add(new Habits(session).habits_as_cards());
        }
        page.add(Html.wrapped().h5().wrap("Actions"));
        page.add(Html.wrapped().ul() //
                .wrap(Html.W().li().wrap(new Payroll(session).getReportPayrollLink(person()))) //
        );
        return finish_pump(page);
    }

    public static void link(RoutingTable routing) throws Exception {
        routing.navbar(DASHBOARD, "Dashboard", Permission.Public);
        routing.get_or_post(DASHBOARD, (session) -> new Dashboard(session).show());
    }
    
    public static SimpleURI DASHBOARD = new SimpleURI("/dashboard");

    public static void link(CounterCodeGen c) {
        c.section("Page: Dashboard");
        c.counter("dashboard_hits", "How many times a dashboard was viewed");
    }

}
