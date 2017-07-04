package farm.bsg.route;

import farm.bsg.data.contracts.ProjectionProvider;

/**
 * This makes it easy to bridge into a given request response and have a single object of concern
 *
 * @author jeffrey
 */
public class DelegateRequest implements RequestResponseWrapper, ProjectionProvider {

    protected final RequestResponseWrapper delegate;

    public DelegateRequest(final RequestResponseWrapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public String first(final String key) {
        return this.delegate.getParam(key);
    }

    @Override
    public String getCookie(final String key) {
        return this.delegate.getCookie(key);
    }

    @Override
    public BinaryFile getFile(final String key) {
        return this.delegate.getFile(key);
    }

    @Override
    public String getParam(final String key) {
        return this.delegate.getParam(key);
    }

    @Override
    public String[] getParamList(final String key) {
        return this.delegate.getParamList(key);
    }

    @Override
    public String getURI() {
        return this.delegate.getURI();
    }

    @Override
    public boolean hasNonNullQueryParam(final String key) {
        return this.delegate.hasNonNullQueryParam(key);
    }

    @Override
    public String[] multiple(final String key) {
        return this.delegate.getParamList(key);
    }

    @Override
    public void redirect(final FinishedHref href) {
        this.delegate.redirect(href);
    }

    @Override
    public void setCookie(final String key, final String value) {
        this.delegate.setCookie(key, value);
    }

}
