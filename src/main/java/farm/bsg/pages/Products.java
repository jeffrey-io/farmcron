package farm.bsg.pages;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Product;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.BinaryFile;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Products extends SessionPage {

    public static SimpleURI PRODUCTS        = new SimpleURI("/admin/products");

    public static SimpleURI PRODUCTS_EDIT   = new SimpleURI("/admin/products;edit");

    public static SimpleURI PRODUCTS_COMMIT = new SimpleURI("/admin/products;edit;commit");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Products");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(PRODUCTS, "Products", Permission.SeeProductsTab);
        routing.get(PRODUCTS, (session) -> new Products(session).list());
        routing.get_or_post(PRODUCTS_EDIT, (session) -> new Products(session).edit());
        routing.post(PRODUCTS_COMMIT, (session) -> new Products(session).commit());
    }

    public Products(final SessionRequest session) {
        super(session, PRODUCTS);
    }

    public String commit() {
        final Product product = pullProduct();
        final BinaryFile file = this.session.getFile("file_image");
        if (file.contentType != null && (file.contentType.equals("image/png") || file.contentType.equals("image/jpeg"))) {
            if (file.bytes != null && file.bytes.length > 0) {
                product.set("image", new String(Base64.encodeBase64(file.bytes)));
                product.set("image_content_type", file.contentType);
                product.set("image_hash", "HASHOFIMAGE");
            }
        }
        this.engine.put(product);
        redirect(PRODUCTS.href());
        return null;
    }

    public String edit() {
        final Product product = pullProduct();

        final Block formInner = Html.block();
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
                .wrap(Html.label("old_price", "Old Price")) //
                .wrap(Html.input("old_price").id_from_name().pull(product).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("file_image", "Image")) //
                .wrap(Html.input("file_image").id_from_name().file()));

        final String imageContentType = product.get("image_content_type");
        final String imageContent = product.get("image");
        if (imageContentType != null) {
            formInner.add(Html.img().content(imageContentType, imageContent).width(400));
        }

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Save").submit()));

        final Block page = Html.block();
        page.add(tabs(PRODUCTS_EDIT.href()));
        page.add(Html.wrapped().h4().wrap("Edit"));
        page.add(Html.form("post", PRODUCTS_COMMIT.href()).multipart().inner(formInner));
        return finish_pump(page);
    }

    public String list() {
        final Table products = new Table("Name", "Category", "Description", "Price");
        for (final Product p : this.engine.select_product().to_list().inline_order_lexographically_asc_by("category", "name").done()) {
            products.row( //
                    Html.link(PRODUCTS_EDIT.href("id", p.getId()), p.get("name")), //
                    p.get("category"), //
                    p.get("description"), //
                    p.get("price"));

        }

        final Block page = Html.block();
        page.add(tabs(PRODUCTS.href()));
        page.add(Html.wrapped().h4().wrap("All Products"));
        page.add(products);
        return finish_pump(page);
    }

    public Product pullProduct() {
        final Product product = query().product_by_id(this.session.getParam("id"), true);
        product.importValuesFromReqeust(this.session, "");
        product.set("who", person().getId());
        return product;
    }

    public HtmlPump tabs(final FinishedHref current) {
        final Link tab1 = Html.link(PRODUCTS.href(), "List All Products").nav_link().active_if_href_is(current);
        final Link tab2 = Html.link(PRODUCTS_EDIT.href("id", UUID.randomUUID().toString()), "Create New Product").nav_link().active_if_href_is(current);
        return Html.nav().pills().with(tab1).with(tab2);
    }
}
