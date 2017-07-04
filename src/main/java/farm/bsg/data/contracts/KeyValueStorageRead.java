package farm.bsg.data.contracts;

import java.util.Map;

import farm.bsg.data.Value;

/**
 * a read only key value storage
 * 
 * @author jeffrey
 */
public interface KeyValueStorageRead {
    /**
     * get the value of the data associated to the key
     * 
     * @param key
     *            the domain of the storage, i.e. the key
     * @return the value if it exists, will return null if it does not exist
     */
    public Value get(String key);

    /**
     * return a map of all pairs of keys and value with the given prefix
     * 
     * @param prefix
     *            the prefix
     * @return a map of all elements in storage where the keys share the given prefix
     */
    public Map<String, Value> scan(String prefix);
}
