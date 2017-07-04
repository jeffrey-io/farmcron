package farm.bsg.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.joda.time.DateTime;

import com.amazonaws.util.json.Jackson;

import farm.bsg.data.contracts.ReadOnlyType;
import farm.bsg.route.RequestResponseWrapper;

public abstract class RawObject {
    private static DateFormat iso() {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df;
    }

    public static String isoTimestamp() {
        return iso().format(new Date());
    }

    public static String isoTimestamp(final long now) {
        return iso().format(new Date(now));
    }

    private final HashMap<String, String> data;

    private final ObjectSchema            schema;

    public RawObject(final ObjectSchema schema) {
        this.schema = schema;
        this.data = new HashMap<>();
    }

    public boolean areAnyNull(final String... keys) {
        for (final String key : keys) {
            if (isNullOrEmpty(key)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(this.data);
    }

    public void copyFrom(final RawObject other, final String... keys) {
        for (final String key : keys) {
            set(key, other.get(key));
        }
    }

    public void generateAndSetId() {
        set("id", UUID.randomUUID().toString());
    }

    public synchronized String get(final String name) {
        final String value = this.data.get(name);
        if (value == null) {
            final ReadOnlyType ty = this.schema.get(name);
            if (ty != null) {
                return ty.defaultValue();
            }
        }
        return value;
    }

    public boolean getAsBoolean(final String name) {
        String value = get(name);
        if (value == null) {
            return false;
        }
        value = value.trim().toLowerCase();
        if (value.equals("yes")) {
            return true;
        }
        if (value.equals("1")) {
            return true;
        }
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("t")) {
            return true;
        }
        return false;
    }

    public double getAsDouble(final String name) {
        final String value = get(name);
        if (value == null) {
            return 0.0;
        }
        if ("".equals(value)) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException nfe) {
            return 0.0;
        }
    }

    public int getAsInt(final String name) {
        String value = this.data.get(name);
        if (value == null) {
            return 0;
        }
        value = value.trim();
        if (value.length() == 0) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public synchronized String getId() {
        return this.data.get("id");
    }

    public String getPrefix() {
        return this.schema.getPrefix();
    }

    public ObjectSchema getSchema() {
        return this.schema;
    }

    public String getStorageKey() {
        return getPrefix() + getId();
    }

    public DateTime getTimestamp(final String field) {
        String value = get(field);
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.equals("")) {
            return null;
        }
        try {
            final Date date = iso().parse(value);
            return new DateTime(date.getTime());
        } catch (final ParseException e) {
            return null;
        }
    }

    public Set<String> getTokenList(final String name) {
        String value = get(name);
        if (value == null) {
            return Collections.emptySet();
        }
        value = value.trim().toLowerCase();
        if ("".equals(value)) {
            return Collections.emptySet();
        }
        final HashSet<String> tokens = new HashSet<>();
        for (String token : value.split(",")) {
            token = token.trim();
            if (token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public List<Type> getTypes() {
        return this.schema.getTypes();
    }

    public void importValuesFromMap(final Map<String, String> map) {
        for (final Entry<String, String> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public void importValuesFromReqeust(final RequestResponseWrapper wrapper, final String prefix) {
        for (final Type ty : getTypes()) {
            // if a reset value was set, then let's use that first. This allows us to handle the case when a checkbox is unset.
            final String reset = wrapper.getParam(prefix + "__reset_" + ty.name());
            if (reset != null) {
                set(ty.name(), reset);
            }

            final String param = wrapper.getParam(prefix + ty.name());
            if (param != null) {
                set(ty.name(), param);
            }
        }
    }

    public void injectValue(final Value value) {
        if (value != null) {
            value.injectInto(this);
        }
    }

    protected abstract void invalidateCache();

    public boolean isNullOrEmpty(final String name) {
        final String value = get(name);
        if (value == null) {
            return true;
        }
        return value.equals("");
    }

    public synchronized <T> boolean set(final String name, final T value) {
        if (value == null) {
            this.data.remove(name);
            return true;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(value);
        String strValue = sb.toString();
        final ReadOnlyType type = this.schema.get(name);
        if (type == null) {
            this.data.put(name, strValue);
            invalidateCache();
            return true;
        }
        strValue = type.normalize(strValue);
        if (strValue == null) {
            this.data.remove(name);
            invalidateCache();
            return true;
        }
        if (type.validate(strValue)) {
            this.data.put(name, strValue);
            invalidateCache();
            return true;
        }
        return false;
    }

    public String toJson() {
        return Jackson.toJsonString(this.data);
    }

    public PutResult validateAndApplyProjection(final Map<String, String> projection) {
        final PutResult result = new PutResult();

        // validate the project
        for (final Entry<String, String> entry : projection.entrySet()) {
            final ReadOnlyType ty = this.schema.get(entry.getKey());
            if (ty == null) {
                result.addFieldFailure(entry.getKey(), "is_not_valid_name");
                result.setTooMuchData();
                continue;
            }
            final String value = entry.getValue();

            // the value is null which means not provided
            if (value == null) {
                continue;
            }

            if (!ty.validate(value)) {
                result.addFieldFailure(ty.name(), "not_valid");
                continue;
            }
        }

        // if it was not successful, return the put result
        if (!result.success()) {
            return result;
        }

        // apply the data
        for (final Entry<String, String> entry : projection.entrySet()) {
            final ReadOnlyType ty = this.schema.get(entry.getKey());
            String value = entry.getValue();
            if (value != null) {
                value = ty.normalize(value);
                if (value == null) {
                    this.data.remove(ty.name());
                } else {
                    this.data.put(ty.name(), value);
                }
            }
        }
        return result;
    }

}
