package farm.bsg.models;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;

import farm.bsg.QueryEngine;
import farm.bsg.Security;
import farm.bsg.Security.Permission;
import farm.bsg.data.Authenticator;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.data.Value;
import farm.bsg.ops.CounterCodeGen;

public class Person extends RawObject {
    public static final ObjectSchema SCHEMA            = ObjectSchema.persisted("person/",                              //
            Field.STRING("login").makeIndex(true),                                                                      // used
            Field.STRING("name").addProjection("contact_info"),                                                         // -
            Field.STRING("phone").addProjection("contact_info").makeIndex(false),                                       // -
            Field.STRING("email").addProjection("contact_info"),                                                        // -

            Field.STRING("address_1").addProjection("contact_info"),                                                    // -
            Field.STRING("address_2").addProjection("contact_info"),                                                    // -
            Field.STRING("city").addProjection("contact_info"),                                                         // -
            Field.STRING("state").addProjection("contact_info"),                                                        // -
            Field.STRING("postal").addProjection("contact_info"),                                                       // -
            Field.STRING("country").addProjection("contact_info"),                                                      // -

            Field.STRING("salt"),                                                                                       // used
            Field.STRING("hash"),                                                                                       // used

            Field.STRING("cookie").makeIndex(false),                                                                    // generated once
            Field.STRING("super_cookie").makeIndex(false),                                                              // generated once
            Field.STRING("notification_token").makeIndex(false),                                                        // token to map SMS, Facebook to user habits
            Field.STRING("notification_uri"),

                                                               Field.STRING("fiscal_timezone"),                         // defaults to PST, used
                                                               Field.NUMBER("default_mileage"),                         // copied; used
                                                               Field.NUMBER("hourly_wage_compesation"),                 // used
                                                               Field.NUMBER("mileage_compensation"),                    // copied

                                                               Field.NUMBER("bonus_target"),                            //
                                                               Field.NUMBER("min_performance_multiplier"),              //
                                                               Field.NUMBER("max_performance_multiplier"),              //
                                                               Field.NUMBER("monthly_benefits"),                        // used
                                                               Field.NUMBER("tax_withholding"),                         // tax witholding

                                                               Field.TOKEN_STRING_LIST("permissions_and_roles")         //
                                                         );

    private boolean                  permissionChecked = false;

    public Person() {
        super(SCHEMA);
        this.permissions = new HashSet<>();
        this.permissionChecked = false;

    }

    private final HashSet<Permission> permissions;

    public void sync(QueryEngine engine) {
        Value v = engine.storage.get(getStorageKey());
        injectValue(v);
        permissions.clear();
        permissions.addAll(Security.parse(get("permissions_and_roles")));
    }

    public boolean has(Permission permission) {
        return this.permissions.contains(permission);
    }

    public void mustHave(Permission permission) {
        if (this.permissions.contains(permission)) {
            return;
        }
        throw new RuntimeException("permission allowed");
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

    public String getFutureMonth(int dMonth) {
        return fiscalTimeZone(getFiscalTimezone(), "yyyyMM").format(DateTime.now().plusMonths(dMonth).toDate());
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

    public void clearPermissionChecked() {
        permissionChecked = false;
    }

    public boolean wasPermissionChecked() {
        return permissionChecked;
    }

}
