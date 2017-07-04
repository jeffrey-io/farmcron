package farm.bsg.route;

import org.slf4j.Logger;

import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.data.Authenticator.AuthResult;
import farm.bsg.models.Person;
import farm.bsg.ops.Logs;

/**
 * A bundled request with an authorization
 *
 * @author jeffrey
 */
public class SessionRequest extends DelegateRequest {
    public static String       AUTH_COOKIE_NAME = "a";
    Logger                     LOG              = Logs.of(SessionRequest.class);
    public final ProductEngine engine;

    private final AuthResult   authResult;

    /**
     *
     * @param engine
     *            the engine powering the data
     * @param delegate
     *            the request and response wrapper
     */
    public SessionRequest(final ProductEngine engine, final RequestResponseWrapper delegate) {
        super(delegate);
        this.engine = engine;
        final String cookie = delegate.getCookie(AUTH_COOKIE_NAME);
        this.authResult = engine.auth.authenticateAdminByCookies(cookie);

        if (this.authResult.person != null) {
            this.authResult.person.sync(engine);
            if (!this.authResult.cookie.equals(cookie)) {
                delegate.setCookie(AUTH_COOKIE_NAME, this.authResult.cookie);
            }
        }
    }

    /**
     * 6
     *
     * @return the associated person object
     */
    public Person getPerson() {
        return this.authResult.person;
    }

    /**
     * does the given person have the permission to do the following action
     *
     * @param permission
     * @return
     */
    public boolean has(final Permission permission) {
        if (this.authResult.person != null) {
            return this.authResult.person.has(permission);
        }
        return false;
    }

    /**
     * @return if the session should be allowed
     */
    public boolean isAllowed() {
        return this.authResult.allowed;
    }
}
