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
    private final HashMap<String, String> data;
    private final ObjectSchema            schema;

    public RawObject(ObjectSchema schema) {
        this.schema = schema;
        this.data = new HashMap<>();
    }

    public ObjectSchema getSchema() {
        return schema;
    }

    public String getPrefix() {
        return schema.getPrefix();
    }

    public void generateAndSetId() {
        set("id", UUID.randomUUID().toString());
    }
    
    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(data);
    }

    public synchronized String getId() {
        return data.get("id");
    }

    public String getStorageKey() {
        return getPrefix() + getId();
    }
    
    public List<Type> getTypes() {
        return schema.getTypes();
    }

    protected abstract void invalidateCache();

    public synchronized <T> boolean set(String name, T value) {
        if (value == null) {
            this.data.remove(name);
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        String strValue = sb.toString();
        ReadOnlyType type = schema.get(name);
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

    public synchronized String get(String name) {
        String value = this.data.get(name);
        if (value == null) {
            ReadOnlyType ty = schema.get(name);
            if (ty != null) {
                return ty.defaultValue();
            }
        }
        return value;
    }
    
    public boolean isNullOrEmpty(String name) {
        String value = get(name);
        if (value == null) {
            return true;
        }
        return value.equals("");
    }

    public Set<String> getTokenList(String name) {
        String value = get(name);
        if (value == null) {
            return Collections.emptySet();
        }
        value = value.trim().toLowerCase();
        if ("".equals(value)) {
            return Collections.emptySet();
        }
        HashSet<String> tokens = new HashSet<>();
        for (String token : value.split(",")) {
            token = token.trim();
            if (token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public double getAsDouble(String name) {
        String value = get(name);
        if (value == null) {
            return 0.0;
        }
        if ("".equals(value)) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return 0.0;
        }
    }

    public boolean getAsBoolean(String name) {
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

    public int getAsInt(String name) {
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

    public void injectValue(Value value) {
        if (value != null) {
            value.injectInto(this);
        }
    }

    public String toJson() {
        return Jackson.toJsonString(data);
    }

    private static DateFormat iso() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df;
    }

    public void copyFrom(RawObject other, String... keys) {
        for (String key : keys) {
            set(key, other.get(key));
        }
    }

    public static String isoTimestamp() {
        return iso().format(new Date());
    }

    public static String isoTimestamp(long now) {
        return iso().format(new Date(now));
    }

    public DateTime getTimestamp(String field) {
        String value = get(field);
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.equals("")) {
            return null;
        }
        try {
            Date date = iso().parse(value);
            return new DateTime(date.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public void importValuesFromReqeust(RequestResponseWrapper wrapper, String prefix) {
        for (Type ty : getTypes()) {
            // if a reset value was set, then let's use that first. This allows us to handle the case when a checkbox is unset.
            String reset = wrapper.getParam(prefix + "__reset_" + ty.name());
            if (reset != null) {
                set(ty.name(), reset);
            }

            String param = wrapper.getParam(prefix + ty.name());
            if (param != null) {
                set(ty.name(), param);
            }
        }
    }
    
    public void importValuesFromMap(Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public PutResult validateAndApplyProjection(Map<String, String> projection) {
        PutResult result = new PutResult();

        // validate the project
        for (Entry<String, String> entry : projection.entrySet()) {
            ReadOnlyType ty = schema.get(entry.getKey());
            if (ty == null) {
                result.addFieldFailure(entry.getKey(), "is_not_valid_name");
                result.setTooMuchData();
                continue;
            }
            String value = entry.getValue();

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
        for (Entry<String, String> entry : projection.entrySet()) {
            ReadOnlyType ty = schema.get(entry.getKey());
            String value = entry.getValue();
            if (value != null) {
                value = ty.normalize(value);
                if (value == null) {
                    data.remove(ty.name());
                } else {
                    data.put(ty.name(), value);
                }
            }
        }
        return result;
    }

}
