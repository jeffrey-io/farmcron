package farm.bsg.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import farm.bsg.data.contracts.KeyValueStorage;

/**
 *
 * @author jeffrey
 */
public class InMemoryStorage implements KeyValueStorage {
    private final HashMap<String, Value> storage;

    public InMemoryStorage() {
        this.storage = new HashMap<>();
    }

    @Override
    public synchronized Value get(final String key) {
        return this.storage.get(key);
    }

    @Override
    public synchronized boolean put(final String key, final Value value) {
        if (value == null) {
            this.storage.remove(key);
        } else {
            this.storage.put(key, value);
        }
        return true;
    }

    @Override
    public synchronized Map<String, Value> scan(final String prefix) {
        final HashMap<String, Value> results = new HashMap<>();
        for (final Entry<String, Value> entry : this.storage.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
}
