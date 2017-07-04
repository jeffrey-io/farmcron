package farm.bsg.data.contracts;

import farm.bsg.data.Value;

/**
 * a write only key value storage
 *
 * @author jeffrey
 */
public interface KeyValueStoragePut {

    /**
     * associate the given value to the key
     *
     * @param key
     *            the domain of the storage, i.e. the key
     * @param value
     *            the range of the storage, i.e. the value
     */
    public boolean put(String key, Value value);
}
