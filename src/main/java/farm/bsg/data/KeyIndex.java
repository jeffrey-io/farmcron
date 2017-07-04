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

    public KeyIndex(final String field, final boolean unique) {
        this.index = new HashMap<String, Set<String>>();
        this.field = field;
        this.unique = unique;
    }

    public synchronized HashSet<String> getIndexKeys() {
        return new HashSet<>(this.index.keySet());
    }

    public synchronized Map<String, String> getInverseMap() {
        final HashMap<String, String> map = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }

    public synchronized Set<String> getKeys(final String value) {
        final Set<String> keys = this.index.get(value);
        if (keys == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(keys);
    }

    private Set<String> getSet(final String indexValue) {
        Set<String> set = this.index.get(indexValue);
        if (set == null) {
            set = new HashSet<>();
            this.index.put(indexValue, set);
        }
        return set;
    }

    @Override
    public synchronized void put(final String key, final Value oldValue, final Value newValue) {
        final String oldIndexValue = Value.getField(oldValue, this.field);
        final String newIndexValue = Value.getField(newValue, this.field);

        // no-op
        if (Value.stringEqualsWithNullChecks(oldIndexValue, newIndexValue)) {
            return;
        }

        // remove from old index
        if (oldIndexValue != null) {
            final Set<String> set = getSet(oldIndexValue);
            set.remove(key);
            if (set.size() == 0) {
                this.index.remove(oldIndexValue);
            }
        }

        // add to new index
        if (newIndexValue != null) {
            getSet(newIndexValue).add(key);
        }
    }

    public synchronized int size() {
        return this.index.size();
    }

    @Override
    public synchronized void validate(final String key, final Value oldValue, final Value newValue, final PutResult result) {
        if (this.unique) {
            final String newIndexValue = Value.getField(newValue, this.field);
            if (this.index.containsKey(newIndexValue)) {
                final String oldIndexValue = Value.getField(oldValue, this.field);
                if (Value.stringEqualsWithNullChecks(oldIndexValue, newIndexValue)) {
                } else {
                    result.addFieldFailure(this.field, "not_unique");
                }
            }
        }
    }

}
