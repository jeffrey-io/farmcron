package farm.bsg.route;

import java.util.HashMap;

public class MockRequestResponseWrapper implements RequestResponseWrapper {

    public String                            uri;
    public final HashMap<String, String>     params;
    public final HashMap<String, String>     cookies;
    public final HashMap<String, BinaryFile> files;
    private String                           redirected = null;

    public MockRequestResponseWrapper() {
        this.params = new HashMap<>();
        this.cookies = new HashMap<>();
        this.files = new HashMap<>();
    }

    @Override
    public BinaryFile getFile(String key) {
        return files.get(key);
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public String getParam(String key) {
        return params.get(key);
    }

    @Override
    public String getCookie(String key) {
        return cookies.get(key);
    }

    @Override
    public boolean hasNonNullQueryParam(String key) {
        String result = getParam(key);
        if (result == null) {
            return false;
        }
        return true;
    }

    @Override
    public void redirect(String uri) {
        this.redirected = uri;
    }

    @Override
    public String[] getParamList(String key) {
        throw new RuntimeException();
    }

    @Override
    public void setCookie(String key, String value) {
    }

}
