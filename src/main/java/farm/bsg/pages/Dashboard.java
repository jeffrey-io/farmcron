package farm.bsg.pages;

import farm.bsg.AlexaCommands;
import farm.bsg.BsgCounters;
import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class Dashboard extends SessionPage {
    public Dashboard(SessionRequest session) {
        super(session, "/dashboard");
    }

    public Object show() {
        BsgCounters.I.dashboard_hits.bump();
        Block page = Html.block();
        if (has(Permission.SeeChoresTab)) {
            page.add(Html.wrapped().h5().wrap("Chore Briefing"));
            page.add(Html.wrapped().p().wrap(AlexaCommands.TOP_CHORES(engine)));
        }
        if (has(Permission.SeeHabitsTab)) {
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
        routing.navbar("/dashboard", "Dashboard", Permission.Public);
        routing.get_or_post("/dashboard", (session) -> new Dashboard(session).show());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Dashboard");
        c.counter("dashboard_hits", "How many times a dashboard was viewed");
    }

}
