package farm.bsg.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import farm.bsg.data.contracts.KeyValuePairLogger;

public class KeyIndex implements KeyValuePairLogger {

    // maps the field to the set of indices that have it
    private final HashMap<String, Set<String>> index;
    private final String                       field;
    private final boolean                      unique;

    public KeyIndex(String field, boolean unique) {
        this.index = new HashMap<String, Set<String>>();
        this.field = field;
        this.unique = unique;
    }

    private Set<String> getSet(String indexValue) {
        Set<String> set = index.get(indexValue);
        if (set == null) {
            set = new HashSet<>();
            index.put(indexValue, set);
        }
        return set;
    }

    public synchronized Set<String> getKeys(String value) {
        Set<String> keys = index.get(value);
        if (keys == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(keys);
    }

    public synchronized HashSet<String> getIndexKeys() {
        return new HashSet<>(index.keySet());
    }

    public synchronized Map<String, String> getInverseMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }

    public synchronized int size() {
        return index.size();
    }

    @Override
    public synchronized void put(String key, Value oldValue, Value newValue) {
        String oldIndexValue = Value.getField(oldValue, field);
        String newIndexValue = Value.getField(newValue, field);

        // no-op
        if (Value.stringEqualsWithNullChecks(oldIndexValue, newIndexValue)) {
            return;
        }

        // remove from old index
        if (oldIndexValue != null) {
            Set<String> set = getSet(oldIndexValue);
            set.remove(key);
            if (set.size() == 0) {
                index.remove(oldIndexValue);
            }
        }

        // add to new index
        if (newIndexValue != null) {
            getSet(newIndexValue).add(key);
        }
    }

    @Override
    public synchronized void validate(String key, Value oldValue, Value newValue, PutResult result) {
        if (unique) {
            String newIndexValue = Value.getField(newValue, field);
            if (index.containsKey(newIndexValue)) {
                String oldIndexValue = Value.getField(oldValue, field);
                if (Value.stringEqualsWithNullChecks(oldIndexValue, newIndexValue)) {
                } else {
                    result.addFieldFailure(field, "not_unique");
                }
            }
        }
    }

}
