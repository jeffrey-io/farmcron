package farm.bsg.pages;

import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.Cart;
import farm.bsg.models.CartItem;
import farm.bsg.models.Product;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.CustomerPage;
import farm.bsg.pages.common.ParameterHelper;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SimpleURI;

public class YourCart extends CustomerPage {
    
    public YourCart(CustomerRequest request) {
        super(request, CART);
    }

    public String add() {
        String productId = request.getParam("pid");
        String cartId = request.getCartId();
        int quantity = ParameterHelper.getIntParamWithDefault(request, "quantity", 1);

        // make sure the cart exists
        Cart cart = engine.cart_by_id(cartId, false);
        if (cart == null) {
            cart = engine.cart_by_id(cartId, true);
            engine.put(cart);
        }
        
        CartItem item = new CartItem();
        item.generateAndSetId();
        item.set("cart", cartId);
        item.set("product", productId);
        item.set("quantity", quantity);
        if (request.hasNonNullQueryParam("customizations")) {
            item.set("customizations", request.getParam("customizations"));
        }
        engine.put(item);
        request.redirect(CART.href().value);
        return null;
    }
    
    public String update() {
        String itemId = request.getParam("item");
        int quantity = ParameterHelper.getIntParamWithDefault(request, "quantity", 1);
        CartItem item = engine.cartitem_by_id(itemId, false);
        if (item != null) {
            if (quantity == 0) {
                engine.del(item);
            } else {
                item.set("quantity", quantity);
                engine.put(item);
            }
        }
        request.redirect(CART.href().value);
        return null;
    }
    
    public String show() {
        Table cart = new Table("Name", "Quantity", "Unit Price", "Total", "Actions");
        double ticketPrice = 0;
        for (CartItem item : engine.select_cartitem().where_cart_eq(request.getCartId()).to_list().done()) {
            Product product = engine.product_by_id(item.get("product"), false);
            if (product != null) {
                int quantity = item.getAsInt("quantity");
                double price = product.getAsDouble("price");
                double total = Math.floor(price * quantity * 100) / 100.0;
                cart.row( //
                        product.get("name"), //
                        item.get("quantity"), //
                        product.get("price"), //
                        total,
                        Html.link(CART_UPDATE.href("item", item.getId(), "quantity", "0"), "remove"));
                ticketPrice += total;
            } else {
                // show there was a removed product.
            }
        }
        Block page = Html.block();
        page.add(Html.wrapped().h4().wrap("Items in Cart"));
        page.add(cart);
        page.add(Html.wrapped().h4().wrap("Total:" + ticketPrice));
        return page.toHtml();
    }
    
    public String checkout()  {
        return null;
    }
    
    public static void link(RoutingTable routing) {
        routing.customer_get(CART, (cr) -> new YourCart(cr).show());
        routing.customer_get_or_post(CART_ADD, (cr) -> new YourCart(cr).add());
        routing.customer_get_or_post(CART_UPDATE, (cr) -> new YourCart(cr).update());
    }
    
    public static final SimpleURI CART = new SimpleURI("/cart");
    public static final SimpleURI CART_ADD = new SimpleURI("/cart;add");
    public static final SimpleURI CART_UPDATE = new SimpleURI("/cart;update");
    
    public static void link(CounterCodeGen c) {
        c.section("Page: Your Cart");
    }
}
