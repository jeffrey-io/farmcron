package farm.bsg.route;

import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.data.Authenticator.AuthResult;
import farm.bsg.models.Person;

/**
 * A bundled request with an authorization
 * 
 * @author jeffrey
 */
public class SessionRequest extends DelegateRequest {
    public final ProductEngine engine;

    private final AuthResult   authResult;

    /**
     * 
     * @param engine
     *            the engine powering the data
     * @param delegate
     *            the request and response wrapper
     */
    public SessionRequest(ProductEngine engine, RequestResponseWrapper delegate) {
        super(delegate);
        this.engine = engine;
        String superCookie = delegate.getParam("_sc");
        String cookie = delegate.getCookie("xs");
        this.authResult = engine.auth.authenticateByCookies(cookie, superCookie);

        if (this.authResult.person != null) {
            this.authResult.person.sync(engine);
            if (!authResult.cookie.equals(cookie)) {
                delegate.setCookie("xs", authResult.cookie);
            }
        }
    }

    /**
     * does the given person have the permission to do the following action
     * 
     * @param permission
     * @return
     */
    public boolean has(Permission permission) {
        if (this.authResult.person != null) {
            return this.authResult.person.has(permission);
        }
        return false;
    }

    /**
     * @return if the session should be allowed
     */
    public boolean isAllowed() {
        return authResult.allowed;
    }

    /**
     * 6
     * 
     * @return the associated person object
     */
    public Person getPerson() {
        return authResult.person;
    }
}
