package farm.bsg.data.contracts;

import java.util.Map;
import java.util.Set;

/**
 * defines the mapping for single character bit masks (i.e. 26 letters and 26 bits)
 * 
 * @author jeffrey
 *
 */
public interface SingleCharacterBitmaskProvider {
    /**
     * @return a map of labels to letters
     */
    public Map<String, String> asMap();

    /**
     * @return a set of the values given the input string
     */
    public Set<String> valuesOf(String data);
}
