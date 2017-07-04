package farm.bsg.route.spark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import farm.bsg.route.BinaryFile;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.RequestResponseWrapper;
import spark.Request;
import spark.Response;

public class SparkBox implements RequestResponseWrapper {
    private final Request                     request;
    private final Response                    response;
    private final boolean                     secure;
    private final HashMap<String, String[]>   body;
    private final HashMap<String, BinaryFile> files;

    public SparkBox(final Request request, final Response response, final boolean secure) {
        this.request = request;
        this.response = response;
        this.secure = secure;
        this.body = new HashMap<>();
        this.files = new HashMap<>();

        final String contentType = request.headers("Content-Type");
        if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {
            final HashMap<String, List<String>> assemble = new HashMap<>();
            final DiskFileItemFactory factory = new DiskFileItemFactory();
            final ServletFileUpload fileUpload = new ServletFileUpload(factory);
            try {
                for (final FileItem item : fileUpload.parseRequest(request.raw())) {
                    if (item.getFieldName() == null) {
                        continue;
                    }
                    final boolean isFile = item.getName() != null;
                    if (isFile) {
                        this.files.put(item.getFieldName(), new BinaryFile(item.getName(), item.getContentType(), item.get()));
                    } else {
                        List<String> members = assemble.get(item.getFieldName());
                        if (members == null) {
                            members = new ArrayList<>();
                            assemble.put(item.getFieldName(), members);
                        }
                        final String str = item.getString();
                        if (str.length() > 0) {
                            members.add(str);
                        }
                    }
                }
                for (final Entry<String, List<String>> entry : assemble.entrySet()) {
                    final int sz = entry.getValue().size();
                    if (sz > 0) {
                        this.body.put(entry.getKey(), entry.getValue().toArray(new String[sz]));
                    }
                }
            } catch (final Exception err) {
                err.printStackTrace();
            }
        }

    }

    @Override
    public String getCookie(final String key) {
        return this.request.cookie(key);
    }

    @Override
    public BinaryFile getFile(final String key) {
        final BinaryFile file = this.files.get(key);
        if (file == null) {
            return null;
        }
        if (file.bytes == null) {
            return null;
        }
        return file;
    }

    @Override
    public String getParam(final String key) {
        if (this.body.containsKey(key)) {
            return this.body.get(key)[0];
        }
        if ("$$referer".equals(key)) {
            return this.request.headers("Referer");
        }
        return this.request.queryParams(key);
    }

    @Override
    public String[] getParamList(final String key) {
        if (this.body.containsKey(key)) {
            return this.body.get(key);
        }
        return this.request.queryParamsValues(key);
    }

    @Override
    public String getURI() {
        return this.request.uri();
    }

    @Override
    public boolean hasNonNullQueryParam(final String key) {
        if (this.body.containsKey(key)) {
            return true;
        }
        return this.request.queryParams().contains(key);
    }

    @Override
    public void redirect(final FinishedHref href) {
        final StringBuilder sb = new StringBuilder();
        if (this.secure) {
            sb.append("https://");
        } else {
            sb.append("http://");
        }
        sb.append(this.request.headers("Host"));
        sb.append(href.value);
        this.response.redirect(sb.toString());
    }

    @Override
    public void setCookie(final String key, final String value) {
        this.response.cookie(key, value);
    }
}
