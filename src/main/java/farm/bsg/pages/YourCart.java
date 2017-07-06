package farm.bsg.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import farm.bsg.EventBus.Event;
import farm.bsg.EventBus.EventPayload;
import farm.bsg.data.UriBlobCache.UriBlob;
import farm.bsg.models.Cart;
import farm.bsg.models.CartItem;
import farm.bsg.models.Product;
import farm.bsg.models.SiteProperties;
import farm.bsg.models.Task;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.CustomerPage;
import farm.bsg.pages.common.ParameterHelper;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SimpleURI;

public class YourCart extends CustomerPage {
    public static final SimpleURI CART        = new SimpleURI("/cart");

    public static final SimpleURI CART_ADD    = new SimpleURI("/cart;add");

    public static final SimpleURI CART_UPDATE = new SimpleURI("/cart;update");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Your Cart");
    }

    public static void link(final RoutingTable routing) {
        routing.customer_get_or_post(CART, (cr) -> new YourCart(cr).show());
        routing.customer_get_or_post(CART_ADD, (cr) -> new YourCart(cr).add());
        routing.customer_get_or_post(CART_UPDATE, (cr) -> new YourCart(cr).update());
    }

    public YourCart(final CustomerRequest request) {
        super(request, CART);
    }

    public String add() {
        final String productId = this.request.getParam("pid");
        final String cartId = this.request.getCartId();
        final int quantity = ParameterHelper.getIntParamWithDefault(this.request, "quantity", 1);

        // make sure the cart exists
        Cart cart = this.engine.cart_by_id(cartId, false);
        if (cart == null) {
            cart = this.engine.cart_by_id(cartId, true);
            this.engine.put(cart);
        }

        final CartItem found = this.engine.select_cartitem().where_cart_eq(this.request.getCartId()).where_product_eq(productId).to_list().first();
        if (found != null) {
            found.set("quantity", found.getAsInt("quantity") + quantity);
            this.engine.put(found);
        } else {
            final CartItem item = new CartItem();
            item.generateAndSetId();
            item.set("cart", cartId);
            item.set("product", productId);
            item.set("quantity", quantity);
            if (this.request.hasNonNullQueryParam("customizations")) {
                item.set("customizations", this.request.getParam("customizations"));
            }
            this.engine.put(item);
        }
        this.request.redirect(CART.href("back", this.request.getParam("$$referer")));
        return null;
    }

    private Object createPhase(final String currentPhase, final String thisPhase, final int step, final String name) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("step", step);
        map.put("name", name);
        map.put("active", thisPhase.equals(currentPhase));
        return map;
    }

    private String injectBack(final HashMap<String, Object> thing, final String phase) {
        String current = CART.href("phase", phase).value;
        if (this.request.hasNonNullQueryParam("back")) {
            final String back = this.request.getParam("back");
            thing.put("back_url", back);
            thing.put("has_back", true);
            current = CART.href("phase", phase, "back", back).value;
        }
        return current;
    }

    public Object show() {
        final UriBlob blob = query().publicBlobCache.get("/*cart.html");
        if (blob == null) {
            return null;
        }
        final ArrayList<Object> items = new ArrayList<>();
        double ticketPrice = 0;
        for (final CartItem item : this.engine.select_cartitem().where_cart_eq(this.request.getCartId()).to_list().done()) {
            final Product product = this.engine.product_by_id(item.get("product"), false);
            if (product != null) {
                int quantity = item.getAsInt("quantity");
                final String updateQuantity = this.request.getParam("q_" + item.getId());
                if (updateQuantity != null) {
                    final int newQuantity = Integer.parseInt(updateQuantity);
                    if (newQuantity != quantity) {
                        quantity = newQuantity;
                        item.set("quantity", Integer.toString(newQuantity));
                        if (newQuantity <= 0) {
                            query().del(item);
                            break;
                        } else {
                            query().put(item);
                        }
                    }
                }
                final double price = product.getAsDouble("price");
                final double total = Math.floor(price * quantity * 100) / 100.0;

                ticketPrice += total;
                final HashMap<String, Object> iMap = new HashMap<>();
                iMap.put("id", item.getId());
                iMap.put("pid", product.getId());
                iMap.put("name", product.get("name"));
                iMap.put("price", price);
                iMap.put("quantity", quantity);
                iMap.put("total", total);
                iMap.put("remove_url", CART_UPDATE.href("item", item.getId(), "quantity", "0").value);
                items.add(iMap);
            }
        }
        final HashMap<String, Object> root = new HashMap<>();
        final SiteProperties properties = query().siteproperties_get();
        String strategy = properties.get("fulfilment_strategy");
        if (strategy == null) {
            strategy = "none";
        }
        final ArrayList<Object> phases = new ArrayList<>();
        int stepCounter = 0;

        String phase = this.request.getParam("phase");
        if (phase == null || "".equals(phase)) {
            phase = "start";
        }

        if ("checkout".equals(phase) && this.request.hasCustomer) {
            final Cart cart = query().cart_by_id(this.request.getCartId(), false);
            if (cart != null) {
                final Task task = new Task();
                task.generateAndSetId();
                task.set("name", "Complete " + ticketPrice + " purchase for " + this.request.customer.get("email"));
                task.set("cart_id", cart.getId());
                task.set("priority", "0");
                task.setState("created");

                // TODO: add a notification token for close.

                query().put(task);
                final EventPayload payload = new EventPayload("'" + task.get("name") + "' was created; only order for: " + ticketPrice);
                this.engine.eventBus.trigger(Event.TaskCreation, payload);
                cart.set("task", task.getId());
                cart.set("state", "wait");
                query().put(cart);
                this.request.generateNewCartId();
            }
            return "Yo, execute the checkout";
        }

        phases.add(createPhase(phase, "start", ++stepCounter, "Shopping Cart"));
        if (!this.request.hasCustomer) {
            phases.add(createPhase(phase, "assoc", ++stepCounter, "Sign In"));
        }

        String nextPhaseAfterAssoc = "assoc";
        switch (strategy) {
            case "both":
                nextPhaseAfterAssoc = "pickup_or_delivery";
                phases.add(createPhase(phase, "pickup_or_delivery", ++stepCounter, "Select Fulfilment"));
                break;
            case "pickup":
                nextPhaseAfterAssoc = "pickup";
                phases.add(createPhase(phase, "pickup", ++stepCounter, "Decide When to Pickup"));
                break;
            case "delivery":
                nextPhaseAfterAssoc = "delivery";
                phases.add(createPhase(phase, "delivery", ++stepCounter, "Delivery Options"));
                break;
            default:
                nextPhaseAfterAssoc = "payment";
                break;
        }

        if ("start".equals(phase)) {
            final HashMap<String, Object> show_cart = new HashMap<>();
            show_cart.put("items", items);
            show_cart.put("total", ticketPrice);
            final String current = injectBack(show_cart, phase);
            String nextPhase = "assoc";
            if (this.request.hasCustomer) {
                nextPhase = nextPhaseAfterAssoc;
            }

            // if I have a customer logged in already, then go right to the next step after that.
            show_cart.put("next_url", CART.href("back", current, "phase", nextPhase).value);
            root.put("cart", show_cart);
            phase = "start";
        }

        if ("assoc".equals(phase)) {
            final HashMap<String, Object> assoc = new HashMap<>();
            injectBack(assoc, phase);
            assoc.put("total", ticketPrice);
            root.put("assoc", assoc);
        }

        if ("pickup_or_delivery".equals(phase)) {
            final HashMap<String, Object> pickup_or_delivery = new HashMap<>();
            injectBack(pickup_or_delivery, phase);
            root.put("pickup_or_delivery", pickup_or_delivery);
        }

        if ("pickup".equals(phase)) {
            final HashMap<String, Object> pickup = new HashMap<>();
            injectBack(pickup, phase);
            root.put("pickup", pickup);
        }

        if ("delivery".equals(phase)) {
            final HashMap<String, Object> delivery = new HashMap<>();
            injectBack(delivery, phase);
            root.put("delivery", delivery);
        }

        if ("payment".equals(phase)) {
            final HashMap<String, Object> payment = new HashMap<>();
            injectBack(payment, phase);
            payment.put("agree_total", ticketPrice);
            payment.put("checkout_url", CART.href("phase", "checkout").value);
            root.put("payment", payment);
        }

        phases.add(createPhase(phase, "payment", ++stepCounter, "Payment"));

        root.put("phases", phases);
        return blob.transform((s) -> {
            try {
                final String template = s.replaceAll(Pattern.quote("%["), "{{").replaceAll(Pattern.quote("]%"), "}}");
                return compiler.compileInline(template).apply(root);
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
    }

    public String update() {
        final String itemId = this.request.getParam("item");
        final int quantity = ParameterHelper.getIntParamWithDefault(this.request, "quantity", 1);
        final CartItem item = this.engine.cartitem_by_id(itemId, false);
        if (item != null) {
            if (quantity == 0) {
                this.engine.del(item);
            } else {
                item.set("quantity", quantity);
                this.engine.put(item);
            }
        }
        this.request.redirect(CART.href());
        return null;
    }
}
