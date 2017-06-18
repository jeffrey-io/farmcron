package farm.bsg.route.spark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import farm.bsg.route.BinaryFile;
import farm.bsg.route.RequestResponseWrapper;
import spark.Request;
import spark.Response;

public class SparkBox implements RequestResponseWrapper {
    private final Request               request;
    private final Response              response;
    private final boolean               secure;
    private HashMap<String, String[]>   body;
    private HashMap<String, BinaryFile> files;

    public SparkBox(Request request, Response response, boolean secure) {
        this.request = request;
        this.response = response;
        this.secure = secure;
        this.body = new HashMap<>();
        this.files = new HashMap<>();

        // apache commons-fileupload to handle file upload

        String contentType = request.headers("Content-Type");
        if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {
            HashMap<String, List<String>> assemble = new HashMap<>();
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            try {
                for (FileItem item : fileUpload.parseRequest(request.raw())) {
                    if (item.getFieldName() == null) {
                        continue;
                    }
                    boolean isFile = item.getName() != null || item.getFieldName().startsWith("file_");
                    if (isFile) {
                        files.put(item.getFieldName(), new BinaryFile(item.getName(), item.getContentType(), item.get()));
                    } else {
                        List<String> members = assemble.get(item.getFieldName());
                        if (members == null) {
                            members = new ArrayList<>();
                            assemble.put(item.getFieldName(), members);
                        }
                        String str = item.getString();
                        if (str.length() > 0) {
                          members.add(str);
                        }
                    }
                }
                for (Entry<String, List<String>> entry : assemble.entrySet()) {
                    int sz = entry.getValue().size();
                    if (sz > 0) {
                        body.put(entry.getKey(), entry.getValue().toArray(new String[sz]));
                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

    }
    
    @Override
    public BinaryFile getFile(String key) {
        return files.get(key);
    }

    @Override
    public String getURI() {
        return request.uri();
    }

    @Override
    public String getParam(String key) {
        if (body.containsKey(key)) {
            return body.get(key)[0];
        }
        return request.queryParams(key);
    }

    @Override
    public boolean hasNonNullQueryParam(String key) {
        if (body.containsKey(key)) {
          return true;
        }
        return request.queryParams().contains(key);
    }

    @Override
    public void setCookie(String key, String value) {
        response.cookie(key, value);
    }

    @Override
    public void redirect(String uri) {
        StringBuilder sb = new StringBuilder();
        if (secure) {
            sb.append("https://");
        } else {
            sb.append("http://");
        }
        sb.append(request.headers("Host"));
        sb.append(uri);
        response.redirect(sb.toString());
    }

    @Override
    public String getCookie(String key) {
        return request.cookie(key);
    }

    @Override
    public String[] getParamList(String key) {
        if (body.containsKey(key)) {
            return body.get(key);
        }
        return request.queryParamsValues(key);
    }
}
