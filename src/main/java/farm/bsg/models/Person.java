package farm.bsg.models;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Hex;

import farm.bsg.data.RawObject;
import farm.bsg.data.Value;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.ProductEngine;
import farm.bsg.Security;
import farm.bsg.Security.Permission;
import farm.bsg.data.Authenticator;
import farm.bsg.data.Field;

public class Person extends RawObject {
    public Person() {
        super("person/", //
                Field.STRING("login").makeIndex(true), // used
                Field.STRING("name"), // -
                Field.STRING("phone").makeIndex(false), // -
                Field.STRING("email"), // -

                Field.STRING("salt"), // used
                Field.STRING("hash"), // used

                Field.STRING("super_cookie").makeIndex(false), // generated once
                Field.STRING("notification_token").makeIndex(false), // token to map SMS, Facebook to user habits
                Field.STRING("notification_uri"),

                Field.STRING("country"), // -
                Field.STRING("fiscal_timezone"), // defaults to PST, used
                Field.NUMBER("default_mileage"), // copied; used
                Field.NUMBER("hourly_wage_compesation"), // used
                Field.NUMBER("mileage_compensation"), // copied

                Field.NUMBER("bonus_target"), //
                Field.NUMBER("min_performance_multiplier"), //
                Field.NUMBER("max_performance_multiplier"), //
                Field.NUMBER("monthly_benefits"), Field.NUMBER("tax_withholding"),

                Field.TOKEN_STRING_LIST("equipment_skills"), //
                Field.TOKEN_STRING_LIST("permissions_and_roles") //
        );
        this.permissions = new HashSet<>();
    }

    private final HashSet<Permission> permissions;

    public void sync(ProductEngine engine) {
        Value v = engine.storage.get(getStorageKey());
        injectValue(v);
        permissions.clear();
        permissions.addAll(Security.parse(get("permissions_and_roles")));
    }

    public boolean has(Permission permission) {
        return this.permissions.contains(permission);
    }

    public String login() {
        return get("login");
    }

    public String getFiscalTimezone() {
        String tz = get("fiscal_timezone");
        if (tz != null) {
            tz = tz.trim();
            if (tz.length() == 0) {
                tz = null;
            }
        }
        if (tz == null) {
            set("fiscal_timezone", "PST");
            return "PST";
        }
        return tz;
    }

    public void setPassword(String password) {
        byte[] salt = SecureRandom.getSeed(16);
        String hash = Authenticator.hash(password, salt);
        set("salt", Hex.encodeHexString(salt));
        set("hash", hash);
    }

    public String getCurrentDay() {
        return fiscalTimeZone(getFiscalTimezone(), "yyyyMMdd").format(new Date());
    }

    public String getCurrentMonth() {
        return fiscalTimeZone(getFiscalTimezone(), "yyyyMM").format(new Date());
    }

    public String getCurrentHour() {
        return fiscalTimeZone(getFiscalTimezone(), "HH").format(new Date());
    }

    private DateFormat fiscalTimeZone(String timezone, String format) {
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        return df;
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Person");
    }

    @Override
    protected void invalidateCache() {
    }

}
