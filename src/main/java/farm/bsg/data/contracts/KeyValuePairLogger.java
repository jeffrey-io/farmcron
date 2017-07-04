package farm.bsg.data.contracts;

import farm.bsg.data.PutResult;
import farm.bsg.data.Value;

/**
 * Defines a key value pair logger
 *
 * @author jeffrey
 */
public interface KeyValuePairLogger {

    /**
     * @param key
     *            ; the key
     * @param oldValue
     *            ; the prior value
     * @param newValue
     *            ; the new value
     */
    public void put(String key, Value oldValue, Value newValue);

    /**
     * validate that inserting the key makes sense
     *
     * @param key
     *            the key to insert
     * @param oldValue
     *            the old value
     * @param newValue
     *            the new value
     * @param result
     *            a place to define why we errored out
     * @return true if the new value can be inserted
     */
    public void validate(String key, Value oldValue, Value newValue, PutResult result);
}
