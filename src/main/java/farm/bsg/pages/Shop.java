package farm.bsg.pages;

import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.Product;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.CustomerPage;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SimpleURI;

public class Shop extends CustomerPage {

    public Shop(CustomerRequest request) {
        super(request, SHOP);
    }
    
    public Object list() {
        Table products = new Table("Name", "Category", "Description", "Price", "Action");
        for (Product p : engine.select_product().to_list().inline_order_lexographically_asc_by("category", "name").done()) {
            products.row( //
                    Html.link("/product-edit?id=" + p.getId(), p.get("name")), //
                    p.get("category"), //
                    p.get("description"), //
                    p.get("price"), //
                    Html.link(YourCart.CART_ADD.href("pid", p.getId()), "Add one to cart"));
                    
        }
        Block page = Html.block();
        page.add(Html.wrapped().h4().wrap("All Products"));
        page.add(products);
        return page.toHtml();
    }
    
    public static void link(RoutingTable routing) {
        routing.customer_get(SHOP, (cr) -> new Shop(cr).list());
    }
    
    public static final SimpleURI SHOP = new SimpleURI("/shop");
    
    public static void link(CounterCodeGen c) {
        c.section("Page: Shop");
    }
}
