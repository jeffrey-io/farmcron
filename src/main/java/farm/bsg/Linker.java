package farm.bsg;

import farm.bsg.pages.Checks;
import farm.bsg.pages.Chores;
import farm.bsg.pages.Dashboard;
import farm.bsg.pages.Events;
import farm.bsg.pages.Habits;
import farm.bsg.pages.Payroll;
import farm.bsg.pages.People;
import farm.bsg.pages.SignIn;
import farm.bsg.pages.Site;
import farm.bsg.pages.Subscriptions;
import farm.bsg.pages.You;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;

public class Linker {

    public static void link(RoutingTable routing, MultiTenantRouter router) throws Exception {
        Dashboard.link(routing);
        Chores.link(routing);
        Events.link(routing);
        Habits.link(routing);
        Subscriptions.link(routing);
        Payroll.link(routing);
        Checks.link(routing);
        People.link(routing);
        SignIn.link(routing, router);
        Site.link(routing);
        You.link(routing);
        router.informRoutingTableBuilt(routing);
        routing.setupTexting();
    }
}
