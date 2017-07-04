package farm.bsg.route;

import java.util.HashMap;
import java.util.Map;

import farm.bsg.ProductEngine;
import farm.bsg.data.Authenticator.AuthResult;

public class MockRequestBuilder {

    public final String                   uri;
    private final ProductEngine           engine;
    private final HashMap<String, String> params;
    private final HashMap<String, String> cookies;
    private AuthResult                    authResult;

    public MockRequestBuilder(final String uri, final ProductEngine engine) {
        this.uri = uri;
        this.engine = engine;
        this.params = new HashMap<>();
        this.cookies = new HashMap<>();
    }

    public AnonymousRequest asAnonymousRequest() {
        return new AnonymousRequest(this.engine, asRequestResponseWrapper());
    }

    public CustomerRequest asCustomerRequest() {
        return new CustomerRequest(this.engine, asRequestResponseWrapper());
    }

    public RequestResponseWrapper asRequestResponseWrapper() {
        return new RequestResponseWrapper() {

            @Override
            public String getCookie(final String key) {
                if (key.equals("xs") && MockRequestBuilder.this.authResult != null) {
                    return MockRequestBuilder.this.authResult.cookie;
                }
                return MockRequestBuilder.this.cookies.get(key);
            }

            @Override
            public BinaryFile getFile(final String key) {
                return null;
            }

            @Override
            public String getParam(final String key) {
                return MockRequestBuilder.this.params.get(key);
            }

            @Override
            public String[] getParamList(final String key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getURI() {
                return MockRequestBuilder.this.uri;
            }

            @Override
            public boolean hasNonNullQueryParam(final String key) {
                return getParam(key) != null;
            }

            @Override
            public void redirect(final FinishedHref href) {
                // redirectedUri = uri;
            }

            @Override
            public void setCookie(final String key, final String value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public SessionRequest asSessionRequest() {
        return new SessionRequest(this.engine, asRequestResponseWrapper());
    }

    public MockRequestBuilder withAdmin() {
        this.authResult = this.engine.auth.authenticateByUsernameAndPassword("admin", "password");
        return this;
    }

    public MockRequestBuilder withParams(final Map<String, String> p) {
        this.params.putAll(p);
        return this;
    }

    public MockRequestBuilder withParams(final String... keyValue) {
        for (int k = 0; k + 1 < keyValue.length; k += 2) {
            this.params.put(keyValue[k], keyValue[k + 1]);
        }
        return this;
    }
}
