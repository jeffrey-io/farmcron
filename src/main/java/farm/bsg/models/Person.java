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
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("person/", //
            Field.STRING("login").makeIndex(true), // used
            Field.STRING("name").addProjection("contact_info"), // -
            Field.STRING("phone").addProjection("contact_info").makeIndex(false), // -
            Field.STRING("email").addProjection("contact_info"), // -

            Field.STRING("address_1").addProjection("contact_info"), // -
            Field.STRING("address_2").addProjection("contact_info"), // -
            Field.STRING("city").addProjection("contact_info"), // -
            Field.STRING("state").addProjection("contact_info"), // -
            Field.STRING("postal").addProjection("contact_info"), // -
            Field.STRING("country").addProjection("contact_info"), // -

            Field.STRING("salt"), // used
            Field.STRING("hash"), // used

            Field.STRING("cookie").makeIndex(false), // generated once
            Field.STRING("super_cookie").makeIndex(false), // generated once
            Field.STRING("notification_token").makeIndex(false), // token to map SMS, Facebook to user habits
            Field.STRING("notification_uri"),

            Field.STRING("fiscal_timezone"), // defaults to PST, used
            Field.NUMBER("default_mileage"), // copied; used
            Field.NUMBER("hourly_wage_compesation"), // used
            Field.NUMBER("mileage_compensation"), // copied
            Field.NUMBER("ideal_weekly_hours"), //
            Field.NUMBER("pto_earning_rate"), //

            Field.NUMBER("bonus_target"), //
            Field.NUMBER("min_performance_multiplier"), //
            Field.NUMBER("max_performance_multiplier"), //
            Field.NUMBER("monthly_benefits"), // used
            Field.NUMBER("tax_withholding"), // tax witholding

            Field.TOKEN_STRING_LIST("permissions_and_roles") //
    );

    public static void link(final CounterCodeGen c) {
        c.section("Data: Person");
    }

    private boolean                   permissionChecked = false;

    private final HashSet<Permission> permissions;

    public Person() {
        super(SCHEMA);
        this.permissions = new HashSet<>();
        this.permissionChecked = false;

    }

    public void clearPermissionChecked() {
        this.permissionChecked = false;
    }

    private DateFormat fiscalTimeZone(final String timezone, final String format) {
        final DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        return df;
    }

    public String getCurrentDay() {
        return fiscalTimeZone(getFiscalTimezone(), "yyyyMMdd").format(new Date());
    }

    public String getCurrentHour() {
        return fiscalTimeZone(getFiscalTimezone(), "HH").format(new Date());
    }

    public String getCurrentMonth() {
        return fiscalTimeZone(getFiscalTimezone(), "yyyyMM").format(new Date());
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

    public String getFutureMonth(final int dMonth) {
        return fiscalTimeZone(getFiscalTimezone(), "yyyyMM").format(DateTime.now().plusMonths(dMonth).toDate());
    }

    public boolean has(final Permission permission) {
        return this.permissions.contains(permission);
    }

    @Override
    protected void invalidateCache() {
    }

    public String login() {
        return get("login");
    }

    public void mustHave(final Permission permission) {
        if (this.permissions.contains(permission)) {
            return;
        }
        throw new RuntimeException("permission allowed");
    }

    public void setPassword(final String password) {
        final byte[] salt = SecureRandom.getSeed(16);
        final String hash = Authenticator.hash(password, salt);
        set("salt", Hex.encodeHexString(salt));
        set("hash", hash);
    }

    public void sync(final QueryEngine engine) {
        final Value v = engine.storage.get(getStorageKey());
        injectValue(v);
        this.permissions.clear();
        this.permissions.addAll(Security.parse(get("permissions_and_roles")));
    }

    public boolean wasPermissionChecked() {
        return this.permissionChecked;
    }

}
