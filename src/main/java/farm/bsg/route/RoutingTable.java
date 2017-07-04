package farm.bsg.route;

import java.util.ArrayList;
import java.util.function.Consumer;

import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.pages.common.NavBar;
import farm.bsg.route.text.TextMessage;
import farm.bsg.route.text.TextRoute;

/**
 * acts as a way to register specific fixed URIs to specific handers.
 *
 * @author jeffrey
 */
public abstract class RoutingTable {

    private final ArrayList<Consumer<NavBar>> latentNavbar;
    private final ArrayList<TextRoute>        textRoutes;

    public RoutingTable() {
        this.latentNavbar = new ArrayList<>();
        this.textRoutes = new ArrayList<>();
    }

    public abstract void customer_get(ControlledURI path, CustomerRoute route);

    public void customer_get_or_post(final ControlledURI path, final CustomerRoute route) {
        customer_get(path, route);
        customer_post(path, route);
    }

    public abstract void customer_post(ControlledURI path, CustomerRoute route);

    public void flushNavbar(final NavBar navbar) {
        for (final Consumer<NavBar> item : this.latentNavbar) {
            item.accept(navbar);
        }
    }

    public abstract void get(ControlledURI path, SessionRoute route);

    public void get_or_post(final ControlledURI path, final SessionRoute route) {
        get(path, route);
        post(path, route);
    }

    public TextMessage handleText(final ProductEngine engine, final TextMessage message) {
        for (final TextRoute route : this.textRoutes) {
            final TextMessage result = route.handle(engine, message);
            if (result != null) {
                return result;
            }
        }
        return message.generateResponse("Failed to understand '" + message.message + "'... Fun message coming soon");
    }

    public void js_api_get(final SimpleURI path, final String... args) {

    }

    public void js_api_post(final SimpleURI path, final String... args) {

    }

    public void navbar(final ControlledURI uri, final String label, final Permission permission) {
        this.latentNavbar.add((navbar) -> {
            navbar.add(uri.href().value, label, permission);
        });
    }

    public abstract void post(ControlledURI path, SessionRoute route);

    public abstract void public_get(ControlledURI path, AnonymousRoute route);

    public void public_get_or_post(final ControlledURI path, final AnonymousRoute route) {
        public_get(path, route);
        public_post(path, route);
    }

    public abstract void public_post(ControlledURI path, AnonymousRoute route);

    public abstract void set_404(AnonymousRoute route);

    public abstract void setupTexting();

    public void text(final TextRoute route) {
        this.textRoutes.add(route);
    }

}
