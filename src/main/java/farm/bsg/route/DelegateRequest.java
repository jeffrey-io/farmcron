package farm.bsg.route;

import farm.bsg.data.contracts.ProjectionProvider;

/**
 * This makes it easy to bridge into a given request response and have a single object of concern
 * 
 * @author jeffrey
 */
public class DelegateRequest implements RequestResponseWrapper, ProjectionProvider {

    protected final RequestResponseWrapper delegate;

    public DelegateRequest(RequestResponseWrapper delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public BinaryFile getFile(String key) {
        return delegate.getFile(key);
    }
    
    @Override
    public String getURI() {
        return delegate.getURI();
    }

    @Override
    public String getParam(String key) {
        return delegate.getParam(key);
    }

    @Override
    public boolean hasNonNullQueryParam(String key) {
        return delegate.hasNonNullQueryParam(key);
    }

    @Override
    public void redirect(String uri) {
        delegate.redirect(uri);
    }

    @Override
    public String getCookie(String key) {
        return delegate.getCookie(key);
    }

    @Override
    public String[] getParamList(String key) {
        return delegate.getParamList(key);
    }

    @Override
    public void setCookie(String key, String value) {
        delegate.setCookie(key, value);
    }

    @Override
    public String[] multiple(String key) {
        return delegate.getParamList(key);
    }

    @Override
    public String first(String key) {
        return delegate.getParam(key);
    }

}
