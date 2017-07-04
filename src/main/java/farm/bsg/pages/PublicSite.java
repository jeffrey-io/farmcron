package farm.bsg.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringEscapeUtils;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;

import farm.bsg.Security.Permission;
import farm.bsg.data.UriBlobCache.UriBlob;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.WakeInputFile;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.AnonymousPage;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.AnonymousRequest;
import farm.bsg.route.BinaryFile;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class PublicSite {

    public static class ActualPublicSite extends AnonymousPage {
        private final AnonymousRequest request;

        public ActualPublicSite(final AnonymousRequest request) {
            super(request, PUBLIC);
            this.request = request;
        }

        Object render() {
            final String uri = this.request.getURI();
            if (uri.startsWith("/*")) {
                return null;
            }
            return this.request.engine.publicBlobCache.get(uri);
        }
    }

    public static class EditingPublicSite extends SessionPage {
        public EditingPublicSite(final SessionRequest session) {
            super(session, PUBLIC);
        }

        public String commit_upload() {
            person().mustHave(Permission.WebMaster);
            final BinaryFile file_1 = this.session.getFile("file_1");
            if (file_1 != null) {
                final WakeInputFile input = new WakeInputFile();
                input.generateAndSetId();
                input.set("filename", file_1.filename);
                input.set("content_type", file_1.contentType);
                input.set("contents", new String(Base64.encodeBase64(file_1.bytes)));
                this.engine.put(input);
                this.session.redirect(PUBLIC.href());
                return null;
            }
            return "NOPE";
        }

        public String commit_upload_json_gz() {
            person().mustHave(Permission.WebMaster);
            final StringBuilder page = new StringBuilder();
            try {
                final BinaryFile file = this.session.getFile("file_gz");
                if (file != null) {
                    final GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(file.bytes));
                    final JsonNode node = Jackson.getObjectMapper().readTree(input);
                    for (int k = 0; k < node.size(); k++) {
                        final JsonNode map = node.get(k);
                        final Iterator<Entry<String, JsonNode>> fields = map.fields();
                        final HashMap<String, String> dictionary = new HashMap<>();
                        while (fields.hasNext()) {
                            final Entry<String, JsonNode> element = fields.next();
                            dictionary.put(element.getKey(), element.getValue().textValue());
                        }
                        handle(dictionary, page);
                        page.append("<br/>");
                    }
                } else {
                    page.append("no file uploaded");
                }
            } catch (final IOException err) {
                page.append("error:" + err.getMessage());
            }
            return page.toString();
        }

        public String create() {
            person().mustHave(Permission.WebMaster);

            final Block formInner = Html.block();

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

            final Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", PUBLIC_CREATE_WAKE_FILE_COMMIT.href()).inner(formInner));
            return finish_pump(page);
        }

        public String create_wake_file() {
            person().mustHave(Permission.WebMaster);
            final WakeInputFile file = new WakeInputFile();
            file.generateAndSetId();
            file.importValuesFromReqeust(this.session, "");
            this.engine.put(file);
            redirect(PUBLIC.href());
            return null;
        }

        public Object download_json_gz() {
            person().mustHave(Permission.WebMaster);
            try {
                final ArrayList<Map<String, String>> backup = new ArrayList<>();
                for (final WakeInputFile input : query().select_wakeinputfile().done()) {
                    backup.add(input.asMap());
                }
                final ByteArrayOutputStream memory = new ByteArrayOutputStream();
                final GZIPOutputStream gzip = new GZIPOutputStream(memory);
                try {
                    gzip.write(Jackson.toJsonString(backup).getBytes(Charsets.UTF_8));
                    gzip.flush();
                    gzip.finish();
                } finally {
                    gzip.close();
                }
                return new UriBlob("application/x-gzip", memory.toByteArray());
            } catch (final IOException ioe) {
                return "failed!";
            }
        }

        public String edit() {
            person().mustHave(Permission.WebMaster);

            final WakeInputFile file = query().wakeinputfile_by_id(this.session.getParam("id"), false);

            final Block formInner = Html.block();
            formInner.add(Html.input("id").pull(file));
            formInner.add(Html.input("contents_text").id_from_name());

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("filename", "File name")) //
                    .wrap(Html.input("filename").id_from_name().pull(file).text()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("content_type", "Content Type")) //
                    .wrap(Html.input("content_type").id_from_name().pull(file).text()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("description", "Description")) //
                    .wrap(Html.input("description").id_from_name().pull(file).textarea(4, 60)));

            final String contentType = file.get("content_type");
            final String aceType = getAceEditableType(contentType);
            if (aceType != null) {
                final String encodedData = file.get("contents");
                String textData = "";
                if (encodedData != null && encodedData.length() > 0) {
                    textData = new String(Base64.decodeBase64(file.get("contents").getBytes()));
                }
                formInner.add("<div id=\"global_ace_editor\">").add(StringEscapeUtils.escapeHtml(textData)).add("</div>");
                formInner.add("<script>enable_global_ace_editor(\"editor_form\", \"contents_text\", \"" + aceType + "\");</script>");
            } else {
                if (file.isImage()) {
                    formInner.add("<img src=\"data:" + contentType + ";base64," + file.get("contents") + "\" />");
                }
                formInner.add(Html.wrapped().form_group() //
                        .wrap(Html.label("file_1", "Repacement File")) //
                        .wrap(Html.input("file_1").id_from_name().file()));
            }

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("delete", "Delete?")) //
                    .wrap(Html.input("delete").id_from_name().checkbox()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Save").submit()));

            final Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", PUBLIC_UPDATE_WAKE_FILE_COMMIT.href()).multipart().withId("editor_form").inner(formInner));

            return finish_pump(page);
        }

        public String getAceEditableType(final String contentType) {
            if (contentType == null) {
                return null;
            }
            if (contentType.equals("text/html")) {
                return "html";
            }
            if (contentType.equals("text/javascript")) {
                return "js";
            }
            if (contentType.equals("text/css")) {
                return "css";
            }
            return null;
        }

        private void handle(final HashMap<String, String> map, final StringBuilder output) {
            final String id = map.get("id");
            if (id != null) {
                map.remove("id"); // we merge by filename, so we remove this;
            }
            final String filename = map.get("filename");
            if (filename == null) {
                output.append("no filename!");
                return;
            }
            if (id == null) {
                return;
            }
            final WakeInputFile byFilename = query().select_wakeinputfile().where_filename_eq(filename).to_list().first();
            if (byFilename != null) {
                output.append("update file:" + filename);
                byFilename.importValuesFromMap(map);
                this.engine.put(byFilename);
                return;
            }
            final WakeInputFile newFile = new WakeInputFile();
            newFile.generateAndSetId();
            newFile.importValuesFromMap(map);
            this.engine.put(newFile);
            output.append("new file:" + filename);

        }

        public String list() {
            person().mustHave(Permission.WebMaster);
            final Table files = new Table("File name", "Description");
            for (final WakeInputFile p : this.engine.select_wakeinputfile().to_list().inline_order_lexographically_asc_by("filename").done()) {
                files.row(Html.link(PUBLIC_WAKE_EDIT.href("id", p.getId()), p.get("filename")), //
                        p.get("description") //
                );
            }

            final Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("All Files"));
            page.add(Html.link(PUBLIC_UPLOAD.href(), "Upload").btn_primary());
            page.add(Html.link(PUBLIC_CREATE_WAKE_FILE.href(), "Create").btn_primary());
            page.add(Html.link(PUBLIC_DOWNLOAD_JSON_GZ.href(), "Download Site").btn_primary());
            page.add(Html.link(PUBLIC_UPLOAD_JSON_GZ.href(), "Upload Site").btn_primary());

            page.add(files);
            return finish_pump(page);
        }

        public String update_wake_file() {
            person().mustHave(Permission.WebMaster);
            final WakeInputFile file = query().wakeinputfile_by_id(this.session.getParam("id"), false);
            if ("true".equals(this.session.getParam("delete"))) {
                this.engine.del(file);
                redirect(PUBLIC.href());
                return null;
            }
            file.importValuesFromReqeust(this.session, "");
            final String cText = this.session.getParam("contents_text");
            if (cText != null) {
                file.set("contents", new String(Base64.encodeBase64(cText.getBytes(Charsets.UTF_8)), Charsets.UTF_8));
            }
            final BinaryFile file_1 = this.session.getFile("file_1");
            if (file_1 != null && file_1.bytes != null && file_1.bytes.length > 0) {
                file.set("content_type", file_1.contentType);
                file.set("contents", new String(Base64.encodeBase64(file_1.bytes)));
            }
            this.engine.put(file);
            redirect(PUBLIC.href());
            return null;
        }

        public String upload() {
            person().mustHave(Permission.WebMaster);
            final Block formInner = Html.block();

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("file_1", "File 1")) //
                    .wrap(Html.input("file_1").id_from_name().file()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Save").submit()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("description", "Description")) //
                    .wrap(Html.input("description").id_from_name().textarea(4, 60)));

            final Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", PUBLIC_UPLOAD_WAKE.href()).multipart().inner(formInner));
            return finish_pump(page);
        }

        public String upload_json_gz() {
            person().mustHave(Permission.WebMaster);
            final Block formInner = Html.block();

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.label("file_gz", "Upload Gzip")) //
                    .wrap(Html.input("file_gz").id_from_name().file()));

            formInner.add(Html.wrapped().form_group() //
                    .wrap(Html.input("submit").id_from_name().value("Save").submit()));

            final Block page = Html.block();
            page.add(Html.wrapped().h4().wrap("Upload"));
            page.add(Html.form("post", PUBLIC_COMMIT_UPLOAD_JSON_GZ.href()).multipart().inner(formInner));
            return finish_pump(page);
        }
    }

    public static SimpleURI PUBLIC                         = new SimpleURI("/admin/public-site");

    public static SimpleURI PUBLIC_UPLOAD                  = new SimpleURI("/admin/public-site;upload-file");
    public static SimpleURI PUBLIC_CREATE_WAKE_FILE        = new SimpleURI("/admin/public-site;create-new-file");
    public static SimpleURI PUBLIC_UPLOAD_WAKE             = new SimpleURI("/admin/public-site;upload-wake-file");

    public static SimpleURI PUBLIC_CREATE_WAKE_FILE_COMMIT = new SimpleURI("/admin/public-site;create-wake-file-commit");
    public static SimpleURI PUBLIC_UPDATE_WAKE_FILE_COMMIT = new SimpleURI("/admin/public-site;update-wake-file-commit");
    public static SimpleURI PUBLIC_WAKE_EDIT               = new SimpleURI("/admin/public-site;public-wake-edit");
    public static SimpleURI PUBLIC_DOWNLOAD_JSON_GZ        = new SimpleURI("/admin/public-site-download.json.gz");

    public static SimpleURI PUBLIC_UPLOAD_JSON_GZ          = new SimpleURI("/admin/public-site;upload;json;gz");
    public static SimpleURI PUBLIC_COMMIT_UPLOAD_JSON_GZ   = new SimpleURI("/admin/public-site;commit;upload.json.gz");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Public Site");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(PUBLIC, "Public Site", Permission.WebMaster);
        routing.set_404((as) -> new ActualPublicSite(as).render());
        routing.get(PUBLIC, (session) -> new EditingPublicSite(session).list());
        routing.get(PUBLIC_UPLOAD, (session) -> new EditingPublicSite(session).upload());
        routing.post(PUBLIC_UPLOAD_WAKE, (session) -> new EditingPublicSite(session).commit_upload());

        routing.get(PUBLIC_CREATE_WAKE_FILE, (session) -> new EditingPublicSite(session).create());
        routing.post(PUBLIC_CREATE_WAKE_FILE_COMMIT, (session) -> new EditingPublicSite(session).create_wake_file());
        routing.post(PUBLIC_UPDATE_WAKE_FILE_COMMIT, (session) -> new EditingPublicSite(session).update_wake_file());

        routing.get(PUBLIC_WAKE_EDIT, (session) -> new EditingPublicSite(session).edit());
        routing.get(PUBLIC_DOWNLOAD_JSON_GZ, (session) -> new EditingPublicSite(session).download_json_gz());
        routing.get(PUBLIC_UPLOAD_JSON_GZ, (session) -> new EditingPublicSite(session).upload_json_gz());
        routing.post(PUBLIC_COMMIT_UPLOAD_JSON_GZ, (session) -> new EditingPublicSite(session).commit_upload_json_gz());
    }
}
