package farm.bsg.pages;

import java.util.UUID;

import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Product;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Products extends SessionPage {

    public Products(SessionRequest session) {
        super(session, PRODUCTS);
    }
    
    public String list() {
        Table products = new Table("Name", "Category", "Description", "Price");
        for (Product p : engine.select_product().to_list().inline_order_lexographically_asc_by("category", "name").done()) {
            products.row( //
                    Html.link(PRODUCTS_EDIT.href("id", p.getId()), p.get("name")), //
                    p.get("category"), //
                    p.get("description"), //
                    p.get("price"));
                    
        }

        Block page = Html.block();
        page.add(tabs("/products"));
        page.add(Html.wrapped().h4().wrap("All Products"));
        page.add(products);
        return finish_pump(page);
    }
    
    public Product pullProduct() {
        Product product= query().product_by_id(session.getParam("id"), true);
        product.importValuesFromReqeust(session, "");
        product.set("who", person().getId());
        return product;
    }

    public HtmlPump tabs(String current) {
        Link tab1 = Html.link("/products", "List All Products").nav_link().active_if_href_is(current);
        Link tab2 = Html.link(PRODUCTS_EDIT.href("id", UUID.randomUUID().toString()), "Create New Product").nav_link().active_if_href_is(current);
        return Html.nav().pills().with(tab1).with(tab2);
    }
    
    public String commit() {
        Product product = pullProduct();
        engine.put(product);
        redirect("/products");
        return null;
    }
    
    public String edit() {
        Product product = pullProduct();

        Block formInner = Html.block();
        formInner.add(Html.input("id").pull(product));
        formInner.add(Html.input("who").pull(product));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(product).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("category", "Category")) //
                .wrap(Html.input("category").id_from_name().pull(product).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(product).textarea(4, 60)));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("price", "Price")) //
                .wrap(Html.input("price").id_from_name().pull(product).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Save").submit()));

        Block page = Html.block();
        page.add(tabs("/product-edit"));
        page.add(Html.wrapped().h4().wrap("Edit"));
        page.add(Html.form("post", "/commit-product").inner(formInner));
        return finish_pump(page);
    }
    
    public static void link(RoutingTable routing) {
        routing.navbar(PRODUCTS, "Products", Permission.SeePeopleTab);
        routing.get(PRODUCTS, (session) -> new Products(session).list());
        
        routing.get_or_post(PRODUCTS_EDIT, (session) -> new Products(session).edit());
        
        routing.post(PRODUCTS_COMMIT, (session) -> new Products(session).commit());
    }
    
    public static SimpleURI PRODUCTS = new SimpleURI("/products");
    public static SimpleURI PRODUCTS_EDIT = new SimpleURI("/product-edit");
    public static SimpleURI PRODUCTS_COMMIT = new SimpleURI("/commit-product");
    

    public static void link(CounterCodeGen c) {
        c.section("Page: Products");
    }
}
