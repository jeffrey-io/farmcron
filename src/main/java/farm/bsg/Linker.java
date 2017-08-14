package farm.bsg;

import farm.bsg.pages.Checks;
import farm.bsg.pages.Customers;
import farm.bsg.pages.Dashboard;
import farm.bsg.pages.Payroll;
import farm.bsg.pages.People;
import farm.bsg.pages.Products;
import farm.bsg.pages.PublicSite;
import farm.bsg.pages.SignIn;
import farm.bsg.pages.Site;
import farm.bsg.pages.Subscriptions;
import farm.bsg.pages.TaskFactoryManagement;
import farm.bsg.pages.Tasks;
import farm.bsg.pages.You;
import farm.bsg.pages.YourCart;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;

public class Linker {

    public static void link(final RoutingTable routing, final MultiTenantRouter router) throws Exception {
        Dashboard.link(routing);
        Subscriptions.link(routing);
        Payroll.link(routing);
        Customers.link(routing);
        Checks.link(routing);
        People.link(routing);
        Products.link(routing);
        PublicSite.link(routing);
        SignIn.link(routing, router);
        Site.link(routing);
        TaskFactoryManagement.link(routing);
        Tasks.link(routing);
        You.link(routing);
        YourCart.link(routing);
        router.informRoutingTableBuilt(routing);
        routing.setupTexting();
    }
}
