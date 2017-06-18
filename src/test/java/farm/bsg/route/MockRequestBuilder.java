package farm.bsg.route;

import java.util.HashMap;
import java.util.Map;

import farm.bsg.ProductEngine;
import farm.bsg.data.Authenticator.AuthResult;

public class MockRequestBuilder {

    public final String                  uri;
    private final ProductEngine           engine;
    private final HashMap<String, String> params;
    private final HashMap<String, String> cookies;
    private AuthResult                    authResult;

    public MockRequestBuilder(String uri, ProductEngine engine) {
        this.uri = uri;
        this.engine = engine;
        this.params = new HashMap<>();
        this.cookies = new HashMap<>();
    }

    public MockRequestBuilder withAdmin() {
        this.authResult = engine.auth.authenticateByUsernameAndPassword("admin", "password");
        return this;
    }

    public MockRequestBuilder withParams(String... keyValue) {
        for (int k = 0; k + 1 < keyValue.length; k += 2) {
            params.put(keyValue[k], keyValue[k + 1]);
        }
        return this;
    }

    public MockRequestBuilder withParams(Map<String, String> p) {
        params.putAll(p);
        return this;
    }

    public RequestResponseWrapper asRequestResponseWrapper() {
        return new RequestResponseWrapper() {

            @Override
            public BinaryFile getFile(String key) {
                return null;
            }

            @Override
            public String getURI() {
                return uri;
            }

            @Override
            public void setCookie(String key, String value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void redirect(String uri) {
                // redirectedUri = uri;
            }

            @Override
            public boolean hasNonNullQueryParam(String key) {
                return getParam(key) != null;
            }

            @Override
            public String[] getParamList(String key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getParam(String key) {
                return params.get(key);
            }

            @Override
            public String getCookie(String key) {
                if (key.equals("xs") && authResult != null) {
                    return authResult.cookie;
                }
                return cookies.get(key);
            }
        };
    }

    public SessionRequest asSessionRequest() {
        return new SessionRequest(engine, asRequestResponseWrapper());
    }

    public AnonymousRequest asAnonymousRequest() {
        return new AnonymousRequest(engine, asRequestResponseWrapper());
    }

    public CustomerRequest asCustomerRequest() {
        return new CustomerRequest(engine, asRequestResponseWrapper());
    }
}
