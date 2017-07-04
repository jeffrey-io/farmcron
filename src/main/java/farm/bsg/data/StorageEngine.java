package farm.bsg.data;

import java.util.Map;

import farm.bsg.data.contracts.KeyValuePairLogger;
import farm.bsg.data.contracts.PersistenceLogger;

public class StorageEngine {

    private final InMemoryStorage    memory;
    private final KeyValuePairLogger logger;
    private final PersistenceLogger  persistence;

    public StorageEngine(final InMemoryStorage memory, final KeyValuePairLogger logger, final PersistenceLogger persistence) throws Exception {
        this.memory = memory;
        this.logger = logger;
        this.persistence = persistence;
        persistence.pump((key, value) -> {
            logger.put(key, null, value);
            memory.put(key, value);
            return true;
        });
    }

    public Value get(final String key) {
        return this.memory.get(key);
    }

    public synchronized PutResult put(final String key, final Value value) {
        return put(key, value, false);
    }

    public synchronized PutResult put(final String key, final Value value, final boolean ephemeral) {
        final PutResult result = new PutResult();
        final Value prior = this.memory.get(key);
        // make sure the write is sane with respect to the indexing
        this.logger.validate(key, prior, value, result);
        // the validation was successful, let's proceed
        if (result.success()) {
            // assume it is valid, now, let's replicate it out
            if (ephemeral) {
                // only store in memory
                this.memory.put(key, value);
                this.logger.put(key, prior, value);
            } else {
                if (this.persistence.put(key, value)) {
                    // local storage should always work
                    this.memory.put(key, value);
                    this.logger.put(key, prior, value);
                } else {
                    result.setFailedStorage();
                }
            }
        }
        return result;
    }

    public Map<String, Value> scan(final String prefix) {
        return this.memory.scan(prefix);
    }
}
