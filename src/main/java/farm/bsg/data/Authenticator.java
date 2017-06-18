package farm.bsg.data;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

import farm.bsg.QueryEngine;
import farm.bsg.models.Person;

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
        AuthResult result = AuthResult.ALLOWED(generateCookie(), p);
        cookieCache.put(result.cookie, result);
        return result;
    }

    public AuthResult authenticateByUsernameAndPassword(String usernameRaw, String password) {
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
                return AuthResult.DENIED();
            }
        }

        if (p == null) {
            return AuthResult.DENIED();
        }

        try {
            byte[] salt = Hex.decodeHex(p.get("salt").toCharArray());
            String computed = hash(password, salt);
            if (MessageDigest.isEqual(computed.getBytes(), p.get("hash").getBytes())) {
                if (splitUsername.length > 1) {
                    return impersonate(p, splitUsername[1]);
                }
                return allow(p);
            }
        } catch (Exception err) {

        }
        return AuthResult.DENIED();
    }

    public AuthResult authenticateByCookies(String cookie, String superCookie) {
        AuthResult result = cookieCache.get(cookie);
        if (result == null) {
            if (superCookie != null) {
                Person p = engine.select_person().where_super_cookie_eq(superCookie).to_list().first();
                if (p != null) {
                    return allow(p);
                }
            }
            return AuthResult.DENIED();
        }
        // refresh the person object
        return result;
    }

    public String generateCookie() {
        String cookie = Hex.encodeHexString(new SecureRandom().generateSeed(16));
        while (cookieCache.containsKey(cookie)) {
            cookie += Hex.encodeHexString(new SecureRandom().generateSeed(2));
        }
        return cookie;
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
}
