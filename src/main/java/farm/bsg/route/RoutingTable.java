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

    public void navbar(String href, String label, Permission permission) {
        latentNavbar.add((navbar) -> {
            navbar.add(href, label, permission);
        });
    }

    public void flushNavbar(NavBar navbar) {
        for (Consumer<NavBar> item : latentNavbar) {
            item.accept(navbar);
        }
    }

    public void text(TextRoute route) {
        this.textRoutes.add(route);
    }

    public TextMessage handleText(ProductEngine engine, TextMessage message) {
        for (TextRoute route : textRoutes) {
            TextMessage result = route.handle(engine, message);
            if (result != null) {
                return result;
            }
        }
        return message.generateResponse("Failed to understand '" + message.message + "'... Fun message coming soon");
    }

    public abstract void setupTexting();

    public abstract void get(String path, SessionRoute route);

    public abstract void post(String path, SessionRoute route);

    public void get_or_post(String path, SessionRoute route) {
        get(path, route);
        post(path, route);
    }
}
