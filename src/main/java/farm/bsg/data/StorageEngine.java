package farm.bsg.data;

import java.util.Map;

import farm.bsg.data.contracts.KeyValuePairLogger;
import farm.bsg.data.contracts.KeyValueStoragePut;
import farm.bsg.data.contracts.PersistenceLogger;

public class StorageEngine {

    private final InMemoryStorage    memory;
    private final KeyValuePairLogger logger;
    private final PersistenceLogger  persistence;

    public StorageEngine(InMemoryStorage memory, KeyValuePairLogger logger, PersistenceLogger persistence) throws Exception {
        this.memory = memory;
        this.logger = logger;
        this.persistence = persistence;
        persistence.pump(new KeyValueStoragePut() {

            @Override
            public boolean put(String key, Value value) {
                logger.put(key, null, value);
                memory.put(key, value);
                return true;
            }
        });
    }

    public Value get(String key) {
        return memory.get(key);
    }

    public Map<String, Value> scan(String prefix) {
        return memory.scan(prefix);
    }

    public synchronized PutResult put(String key, Value value) {
        return put(key, value, false);
    }
    
    public synchronized PutResult put(String key, Value value, boolean ephemeral) {
        PutResult result = new PutResult();
        Value prior = memory.get(key);
        // make sure the write is sane with respect to the indexing
        logger.validate(key, prior, value, result);
        // the validation was successful, let's proceed
        if (result.success()) {
            // assume it is valid, now, let's replicate it out
            if (ephemeral) {
                // only store in memory
                memory.put(key, value);
                logger.put(key, prior, value);
            } else {
                if (persistence.put(key, value)) {
                    // local storage should always work
                    memory.put(key, value);
                    logger.put(key, prior, value);
                } else {
                    result.setFailedStorage();
                }
            }
        }
        return result;
    }
}
