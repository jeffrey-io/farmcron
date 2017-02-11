package farm.bsg.data;

import java.util.Iterator;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

public class Value {

    private String   value;
    private JsonNode parsed;

    public Value(String value) {
        this.value = value;
        this.parsed = Jackson.fromJsonString(value, JsonNode.class);
    }

    public boolean injectInto(RawObject o) {
        Iterator<String> it = parsed.fieldNames();
        boolean result = true;
        while (it.hasNext()) {
            String key = it.next();
            if (!o.set(key, parsed.get(key).asText())) {
                result = false;
            }
        }
        return result;
    }

    private String getFieldWithoutNullCheck(String key) {
        JsonNode node = parsed.get(key);
        if (node == null) {
            return null;
        }
        if (node.isNull()) {
            return null;
        }
        return node.asText();
    }

    public byte[] getBytes() {
        return value.getBytes();
    }

    @Override
    public String toString() {
        return value;
    }

    public static String getField(Value v, String key) {
        if (v == null) {
            return null;
        }
        return v.getFieldWithoutNullCheck(key);
    }

    public static boolean stringEqualsWithNullChecks(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a != null && b != null) {
            return a.equals(b);
        }
        return false;
    }


    public static int stringCompareWithNullChecks(String a, String b, boolean caseSensitive) {
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
}
