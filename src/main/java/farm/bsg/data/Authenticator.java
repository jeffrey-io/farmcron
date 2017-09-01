package farm.bsg.data;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;

import farm.bsg.BsgCounters;
import farm.bsg.QueryEngine;
import farm.bsg.models.Customer;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.ops.Logs;

/**
 * Defines how authentication works
 *
 * @author jeffrey
 */
public class Authenticator {
    public static class AuthResult {
        public static AuthResult ALLOWED(final String cookie, final Person person) {
            return new AuthResult(true, cookie, person);
        }

        public static AuthResult DENIED() {
            return new AuthResult(false, null, null);
        }

        public final boolean allowed;

        public final String  cookie;

        public final Person  person;

        private AuthResult(final boolean allowed, final String cookie, final Person person) {
            this.allowed = allowed;
            this.person = person;

            this.cookie = cookie;
        }
    }

    public static class AuthResultCustomer {
        public static AuthResultCustomer ALLOWED(final String cookie, final Customer person) {
            return new AuthResultCustomer(true, cookie, person);
        }

        public static AuthResultCustomer DENIED() {
            return new AuthResultCustomer(false, null, null);
        }

        public final boolean  allowed;

        public final String   cookie;

        public final Customer customer;

        private AuthResultCustomer(final boolean allowed, final String cookie, final Customer customer) {
            this.allowed = allowed;
            this.customer = customer;
            this.cookie = cookie;
        }
    }

    public static String hash(final String password, final byte[] salt) {
        try {
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            final PBEKeySpec spec = new PBEKeySpec(password.toLowerCase().toCharArray(), salt, 1024, 256);
            final SecretKey key = skf.generateSecret(spec);
            final byte[] res = key.getEncoded();
            return Hex.encodeHexString(res);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void link(final CounterCodeGen c) {
        c.section("Auth");
        c.counter("auth_login_attempt", "an auth was attempted");
        c.counter("auth_login_success", "an auth attempt was successful");
        c.counter("auth_login_failure", "an auth attempt failed");

        c.counter("auth_customer_login_attempt", "an auth was attempted");
        c.counter("auth_customer_login_success", "an auth attempt was successful");
        c.counter("auth_customer_login_failure", "an auth attempt failed");

        c.counter("auth_attempt_cookie", "an auth was attempted");
        c.counter("auth_cache_hit", "the cookie was found in the local cache");
        c.counter("auth_cache_populate", "the cookie was found in the DB and went into local cache");
        c.counter("auth_super_cookie_conversion", "a super cookie was converted into a new cookie");

        c.counter("auth_attempt_token", "an auth was attempted by token");
        c.counter("auth_token_found", "a token for  device auth was found");
        c.counter("auth_token_notfound", "a token for device auth was not found");

    }

    Logger                                            LOG = Logs.of(Authenticator.class);

    private final QueryEngine                         engine;

    private final HashMap<String, AuthResult>         cookieCacheAdmins;

    private final HashMap<String, AuthResultCustomer> cookieCacheCustomer;

    public Authenticator(final QueryEngine engine) {
        this.engine = engine;
        this.cookieCacheAdmins = new HashMap<String, Authenticator.AuthResult>();
        this.cookieCacheCustomer = new HashMap<String, Authenticator.AuthResultCustomer>();
    }

    private AuthResultCustomer allow(final Customer c, final String cookie) {
        final AuthResultCustomer result = AuthResultCustomer.ALLOWED(cookie, c);
        this.cookieCacheCustomer.put(result.cookie, result);
        return result;
    }

    private AuthResult allow(final Person p) {
        final AuthResult result = AuthResult.ALLOWED(generateCookie(p.getId()), p);
        this.cookieCacheAdmins.put(result.cookie, result);
        return result;
    }

    private AuthResult allow(final Person p, final String cookie) {
        final AuthResult result = AuthResult.ALLOWED(cookie, p);
        this.cookieCacheAdmins.put(result.cookie, result);
        return result;
    }

    public AuthResult authenticateAdminByCookies(final String cookie) {
        BsgCounters.I.auth_attempt_cookie.bump();
        final AuthResult result = this.cookieCacheAdmins.get(cookie);
        if (result == null) {
            if (cookie != null) {
                final Person p = this.engine.select_person().where_cookie_eq(cookie).to_list().first();
                if (p != null) {
                    BsgCounters.I.auth_cache_populate.bump();
                    return allow(p, cookie);
                }
            }
            return AuthResult.DENIED();
        }
        BsgCounters.I.auth_cache_hit.bump();
        // refresh the person object
        return result;
    }

    public Person authenticateByDeviceToken(final String deviceToken) {
        return this.engine.select_person().where_device_token_eq(deviceToken).to_list().first();
    }

    public AuthResult authenticateByUsernameAndPassword(final String usernameRaw, final String password) {
        BsgCounters.I.auth_login_attempt.bump();
        final String[] splitUsername = usernameRaw.toLowerCase().split(":");
        final String username = splitUsername[0].trim();
        final Person p = this.engine.select_person().where_login_eq(username).to_list().first();

        if (p == null) {
            BsgCounters.I.auth_login_failure.bump();
            return AuthResult.DENIED();
        }

        try {
            final byte[] salt = Hex.decodeHex(p.get("salt").toCharArray());
            final String computed = hash(password, salt);
            if (MessageDigest.isEqual(computed.getBytes(), p.get("hash").getBytes())) {
                if (splitUsername.length > 1) {
                    return impersonate(p, splitUsername[1]);
                }
                final String cookie = generateCookie(p.getId());
                p.set("cookie", cookie);
                this.engine.put(p);
                BsgCounters.I.auth_login_success.bump();
                return allow(p, cookie);
            }
        } catch (final Exception err) {

        }
        BsgCounters.I.auth_login_failure.bump();
        return AuthResult.DENIED();
    }

    public AuthResultCustomer authenticateCustomer(final String email, final String password) {
        final Customer customer = this.engine.select_customer().where_email_eq(email).to_list().first();

        if (customer == null) {
            BsgCounters.I.auth_customer_login_failure.bump();
            return AuthResultCustomer.DENIED();
        }

        try {
            final byte[] salt = Hex.decodeHex(customer.get("salt").toCharArray());
            final String computed = hash(password, salt);
            if (MessageDigest.isEqual(computed.getBytes(), customer.get("hash").getBytes())) {
                final String cookie = generateCookie(customer.getId());
                customer.set("cookie", cookie);
                this.engine.put(customer);
                BsgCounters.I.auth_customer_login_success.bump();
                return allow(customer, cookie);
            }
        } catch (final Exception err) {

        }
        BsgCounters.I.auth_customer_login_failure.bump();
        return AuthResultCustomer.DENIED();
    }

    public AuthResultCustomer authenticateCustomerByCookies(final String cookie) {
        BsgCounters.I.auth_attempt_cookie.bump();
        final AuthResultCustomer result = this.cookieCacheCustomer.get(cookie);
        if (result == null) {
            if (cookie != null) {
                final Customer customer = this.engine.select_customer().where_cookie_eq(cookie).to_list().first();
                if (customer != null) {
                    BsgCounters.I.auth_cache_populate.bump();
                    return allow(customer, cookie);
                }
            }
            return AuthResultCustomer.DENIED();
        }
        BsgCounters.I.auth_cache_hit.bump();
        // refresh the person object
        return result;
    }

    public String generateCookie(final String userId) {
        String cookie = Hex.encodeHexString(new SecureRandom().generateSeed(32));
        while (this.cookieCacheAdmins.containsKey(cookie) || this.cookieCacheCustomer.containsKey(cookie)) {
            cookie += Hex.encodeHexString(new SecureRandom().generateSeed(2));
        }
        return userId + "_" + cookie;
    }

    private AuthResult impersonate(final Person authenticatedPerson, final String usernameToImpersonate) {
        final Person p = this.engine.select_person().where_login_eq(usernameToImpersonate).to_list().first();
        if (p == null) {
            return AuthResult.DENIED();
        }
        return allow(p);
    }
}
