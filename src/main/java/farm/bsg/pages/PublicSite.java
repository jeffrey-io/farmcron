package farm.bsg.pages;



import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.Product;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.AnonymousPage;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class PublicSite {

    public static class ActualPublicSite extends AnonymousPage {
        private final AnonymousRequest request;

        public ActualPublicSite(AnonymousRequest request) {
            super(request, "/*");
            this.request = request;
        }

        String render() {
            return "Hello World:" + request.getURI();
        }
    }

    public static class EditingPublicSite extends SessionPage {
        public EditingPublicSite(SessionRequest session) {
            super(session, "/public");
        }

        public String list() {
            Table files = new Table("File name");
            for (Product p : engine.select_product().to_list().inline_order_lexographically_asc_by("filename").done()) {
                files.row(Html.link("/upload-edit?id=" + p.getId(), p.get("filename")));

            }

            Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("All Files"));
            page.add(Html.link("/public-upload", "Upload").btn_primary());
            page.add(files);
            return finish_pump(page);
        }
        
        public String upload() {
            Block formInner = Html.block();

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("file1", "File 1")) //
                    .wrap(Html.input("file1").id_from_name().file()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Save").submit()));


            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("description", "Description")) //
                    .wrap(Html.input("description").id_from_name().textarea(4, 60)));

            
            Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", "/upload-wake-file").multipart().inner(formInner));
            return finish_pump(page);
        }
        
        public String commit_upload() {
            String wut = session.getParam("file1");
            return "umm... ok:" + wut + "//" + session.getParam("description");
        }
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/public", "Public Site", Permission.CheckMake);
        routing.set_404((as) -> new ActualPublicSite(as).render());
        routing.get("/public", (session) -> new EditingPublicSite(session).list());
        routing.get("/public-upload", (session) -> new EditingPublicSite(session).upload());
        routing.post("/upload-wake-file", (session) -> new EditingPublicSite(session).commit_upload());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Public Site");
    }
}
