package farm.bsg.route;

import java.util.HashMap;

public class MockRequestResponseWrapper implements RequestResponseWrapper {

    public String                            uri;
    public final HashMap<String, String>     params;
    public final HashMap<String, String>     cookies;
    public final HashMap<String, BinaryFile> files;
    private FinishedHref                     redirected = null;

    public MockRequestResponseWrapper() {
        this.params = new HashMap<>();
        this.cookies = new HashMap<>();
        this.files = new HashMap<>();
    }

    @Override
    public String getCookie(final String key) {
        return this.cookies.get(key);
    }

    @Override
    public BinaryFile getFile(final String key) {
        return this.files.get(key);
    }

    @Override
    public String getParam(final String key) {
        return this.params.get(key);
    }

    @Override
    public String[] getParamList(final String key) {
        throw new RuntimeException();
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public boolean hasNonNullQueryParam(final String key) {
        final String result = getParam(key);
        if (result == null) {
            return false;
        }
        return true;
    }

    @Override
    public void redirect(final FinishedHref href) {
        this.redirected = href;
    }

    @Override
    public void setCookie(final String key, final String value) {
    }

}
