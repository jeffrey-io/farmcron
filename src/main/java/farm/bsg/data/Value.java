package farm.bsg.data;

import java.util.Iterator;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

public class Value {

    public static String getField(final Value v, final String key) {
        if (v == null) {
            return null;
        }
        return v.getFieldWithoutNullCheck(key);
    }

    public static int stringCompareWithNullChecks(final String a, final String b, final boolean caseSensitive) {
        if (a == null && b == null) {
            return 0;
        }
        if (a != null && b != null) {
            if (caseSensitive) {
                return a.compareTo(b);
            } else {
                return a.compareToIgnoreCase(b);
            }
        }
        if (a == null) {
            return -1;
        } else {
            return 1;
        }
    }

    public static boolean stringEqualsWithNullChecks(final String a, final String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a != null && b != null) {
            return a.equals(b);
        }
        return false;
    }

    private final String   value;

    private final JsonNode parsed;

    public Value(final String value) {
        this.value = value;
        this.parsed = Jackson.fromJsonString(value, JsonNode.class);
    }

    public byte[] getBytes() {
        return this.value.getBytes();
    }

    private String getFieldWithoutNullCheck(final String key) {
        final JsonNode node = this.parsed.get(key);
        if (node == null) {
            return null;
        }
        if (node.isNull()) {
            return null;
        }
        return node.asText();
    }

    public boolean injectInto(final RawObject o) {
        final Iterator<String> it = this.parsed.fieldNames();
        boolean result = true;
        while (it.hasNext()) {
            final String key = it.next();
            if (!o.set(key, this.parsed.get(key).asText())) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
