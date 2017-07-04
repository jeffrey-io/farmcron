package farm.bsg.data.contracts;

import farm.bsg.data.Value;

/**
 * Assumes we have a mechanism
 *
 * @author jeffrey
 */
public interface PersistenceLogger {

    public void pump(KeyValueStoragePut storage) throws Exception;

    public boolean put(String key, Value newValue);

}
