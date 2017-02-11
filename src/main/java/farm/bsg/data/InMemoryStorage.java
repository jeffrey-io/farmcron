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
    public synchronized Value get(String key) {
        return storage.get(key);
    }

    @Override
    public synchronized Map<String, Value> scan(String prefix) {
        HashMap<String, Value> results = new HashMap<>();
        for (Entry<String, Value> entry : storage.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }

    @Override
    public synchronized boolean put(String key, Value value) {
        if (value == null) {
            this.storage.remove(key);
        } else {
            this.storage.put(key, value);
        }
        return true;
    }
}
