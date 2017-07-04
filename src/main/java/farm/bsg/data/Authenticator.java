package farm.bsg.data;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

import farm.bsg.BsgCounters;
import farm.bsg.QueryEngine;
import farm.bsg.models.Person;
import farm.bsg.ops.CounterCodeGen;

/**
 * Defines how authentication works
 * 
 * @author jeffrey
 */
public class Authenticator {
    private final QueryEngine                 engine;
    private final HashMap<String, AuthResult> cookieCache;

    public Authenticator(QueryEngine engine) {
        this.engine = engine;
        this.cookieCache = new HashMap<String, Authenticator.AuthResult>();
    }

    public static class AuthResult {
        public final boolean allowed;
        public final String  cookie;
        public final Person  person;

        private AuthResult(boolean allowed, String cookie, Person person) {
            this.allowed = allowed;
            this.person = person;

            this.cookie = cookie;
        }

        public static AuthResult DENIED() {
            return new AuthResult(false, null, null);
        }

        public static AuthResult ALLOWED(String cookie, Person person) {
            return new AuthResult(true, cookie, person);
        }
    }

    private AuthResult impersonate(Person authenticatedPerson, String usernameToImpersonate) {
        Person p = engine.select_person().where_login_eq(usernameToImpersonate).to_list().first();
        if (p == null) {
            return AuthResult.DENIED();
        }
        return allow(p);
    }

    private AuthResult allow(Person p) {
        AuthResult result = AuthResult.ALLOWED(generateCookie(p.getId()), p);
        cookieCache.put(result.cookie, result);
        return result;
    }

    private AuthResult allow(Person p, String cookie) {
        AuthResult result = AuthResult.ALLOWED(cookie, p);
        cookieCache.put(result.cookie, result);
        return result;
    }

    public AuthResult authenticateByUsernameAndPassword(String usernameRaw, String password) {
        BsgCounters.I.auth_attempt_login.bump();
        String[] splitUsername = usernameRaw.toLowerCase().split(":");
        String username = splitUsername[0].trim();
        Person p = engine.select_person().where_login_eq(username).to_list().first();

        if (p == null && username.equals("admin")) {
            boolean noUsers = engine.storage.scan("person/").size() == 0;
            if (noUsers) {
                p = new Person();
                p.generateAndSetId();
                p.set("login", "admin");
                p.set("name", "Generic Administrator");
                p.set("permissions_and_roles", "god");
                p.setPassword("default_password_42"); //
                engine.put(p);
            } else {
                BsgCounters.I.auth_attempt_login_failure.bump();
                return AuthResult.DENIED();
            }
        }

        if (p == null) {
            BsgCounters.I.auth_attempt_login_failure.bump();
            return AuthResult.DENIED();
        }

        try {
            byte[] salt = Hex.decodeHex(p.get("salt").toCharArray());
            String computed = hash(password, salt);
            if (MessageDigest.isEqual(computed.getBytes(), p.get("hash").getBytes())) {
                if (splitUsername.length > 1) {
                    return impersonate(p, splitUsername[1]);
                }
                String cookie = generateCookie(p.getId());
                p.set("cookie", cookie);
                engine.put(p);
                BsgCounters.I.auth_attempt_login_success.bump();
                return allow(p, cookie);
            }
        } catch (Exception err) {

        }
        BsgCounters.I.auth_attempt_login_failure.bump();
        return AuthResult.DENIED();
    }

    public AuthResult authenticateByCookies(String cookie, String superCookie) {
        BsgCounters.I.auth_attempt_cookie.bump();
        AuthResult result = cookieCache.get(cookie);
        if (result == null) {
            if (cookie != null) {
                Person p = engine.select_person().where_cookie_eq(cookie).to_list().first();
                if (p != null) {
                    BsgCounters.I.auth_cache_populate.bump();
                    return allow(p, cookie);
                }
            }
            if (superCookie != null) {
                Person p = engine.select_person().where_super_cookie_eq(superCookie).to_list().first();
                if (p != null) {
                    cookie = generateCookie(p.getId());
                    p.set("cookie", cookie);
                    engine.put(p);
                    BsgCounters.I.auth_super_cookie_conversion.bump();
                    return allow(p, cookie);
                }
            }
            return AuthResult.DENIED();
        }
        BsgCounters.I.auth_cache_hit.bump();
        // refresh the person object
        return result;
    }

    public String generateCookie(String userId) {
        String cookie = Hex.encodeHexString(new SecureRandom().generateSeed(32));
        while (cookieCache.containsKey(cookie)) {
            cookie += Hex.encodeHexString(new SecureRandom().generateSeed(2));
        }
        return userId + "_" + cookie;
    }

    public static String hash(final String password, final byte[] salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toLowerCase().toCharArray(), salt, 1024, 256);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return Hex.encodeHexString(res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void link(CounterCodeGen c) {
        c.section("Auth");
        c.counter("auth_attempt_login", "an auth was attempted");
        c.counter("auth_attempt_login_success", "an auth attempt was successful");
        c.counter("auth_attempt_login_failure", "an auth attempt failed");
        c.counter("auth_attempt_cookie", "an auth was attempted");
        c.counter("auth_cache_hit", "the cookie was found in the local cache");
        c.counter("auth_cache_populate", "the cookie was found in the DB and went into local cache");
        c.counter("auth_super_cookie_conversion", "a super cookie was converted into a new cookie");
    }
}
