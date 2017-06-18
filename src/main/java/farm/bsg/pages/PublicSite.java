package farm.bsg.pages;



import org.apache.commons.codec.binary.Base64;

import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.WakeInputFile;
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
            Table files = new Table("File name", "Description");
            for (WakeInputFile p : engine.select_wakeinputfile().to_list().inline_order_lexographically_asc_by("filename").done()) {
                files.row(Html.link("/public-wake-edit?id=" + p.getId(), p.get("filename")), //
                        p.get("description") //
                        );

            }

            Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("All Files"));
            page.add(Html.link("/public-upload", "Upload").btn_primary());
            page.add(Html.link("/create-wake-file", "Create").btn_primary());
            page.add(files);
            return finish_pump(page);
        }
        
        public String upload() {
            Block formInner = Html.block();

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("file_1", "File 1")) //
                    .wrap(Html.input("file_1").id_from_name().file()));

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
        
        public String create() {
            Block formInner = Html.block();

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("filename", "File name")) //
                    .wrap(Html.input("filename").id_from_name().text()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("content_type", "Content Type")) //
                    .wrap(Html.input("content_type").id_from_name().text()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("description", "Description")) //
                    .wrap(Html.input("description").id_from_name().textarea(4, 60)));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Save").submit()));

            
            Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", "/create-wake-file-commit").inner(formInner));
            return finish_pump(page);
        }
        
        public String edit() {
            WakeInputFile file = query().wakeinputfile_by_id(session.getParam("id"), false);

            Block formInner = Html.block();
            formInner.add(Html.input("id").pull(file));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("filename", "File name")) //
                    .wrap(Html.input("filename").id_from_name().pull(file).text()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("content_type", "Content Type")) //
                    .wrap(Html.input("content_type").id_from_name().pull(file).text()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("description", "Description")) //
                    .wrap(Html.input("description").id_from_name().pull(file).textarea(4, 60)));

            formInner.add("<style type=\"text/css\" media=\"screen\"> #editor { width:100%; height:500px; } </style>");
            formInner.add("<script src=\"https://cdn.jsdelivr.net/ace/1.2.6/min/ace.js\" type=\"text/javascript\" charset=\"utf-8\"></script>");
            String encodedData = file.get("contents");

            String textData = "Hello World";
            if (encodedData != null && encodedData.length() > 0) {
              textData = new String(Base64.decodeBase64(file.get("contents").getBytes()));
            }
            formInner.add("<div id=\"editor\">").add(textData).add("</div>");
            formInner.add("<script>var editor = ace.edit(\"editor\");editor.setTheme(\"ace/theme/monokai\");editor.getSession().setMode(\"ace/mode/html\");</script>");

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Save").submit()));
            
            Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", "/update-wake-file-commit").inner(formInner));
            
            return finish_pump(page);
        }
        
        public String update_wake_file() {
            WakeInputFile file = query().wakeinputfile_by_id(session.getParam("id"), false);
            file.importValuesFromReqeust(session, "");
            engine.put(file);
            redirect("/public");
            return null;
        }

        public String create_wake_file() {
            WakeInputFile file = new WakeInputFile();
            file.generateAndSetId();
            file.importValuesFromReqeust(session, "");
            engine.put(file);
            redirect("/public");
            return null;
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

        
        routing.get("/create-wake-file", (session) -> new EditingPublicSite(session).create());
        routing.post("/create-wake-file-commit", (session) -> new EditingPublicSite(session).create_wake_file());
        routing.post("/update-wake-file-commit", (session) -> new EditingPublicSite(session).update_wake_file());
        
        routing.get("/public-wake-edit", (session) -> new EditingPublicSite(session).edit());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Public Site");
    }
}
