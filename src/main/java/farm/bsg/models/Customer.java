package farm.bsg.models;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

import farm.bsg.data.Authenticator;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Customer extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("customer/", //
            Field.STRING("email").makeIndex(true), // used
            Field.STRING("name"), // -
            Field.STRING("phone").makeIndex(false), // -

            Field.STRING("salt"), // used
            Field.STRING("hash"), // used
            Field.STRING("cookie").makeIndex(false), // used

            Field.STRING("notification_token").makeIndex(false), // token to map SMS, Facebook
            Field.STRING("notification_uri"));

    public static void link(final CounterCodeGen c) {
        c.section("Data: Customers");
    }

    public Customer() {
        super(SCHEMA);
    }

    @Override
    protected synchronized void invalidateCache() {
    }

    public void setPassword(final String password) {
        final byte[] salt = SecureRandom.getSeed(16);
        final String hash = Authenticator.hash(password, salt);
        set("salt", Hex.encodeHexString(salt));
        set("hash", hash);
    }
}
