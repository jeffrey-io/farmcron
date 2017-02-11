package farm.bsg.route.text;

import java.util.HashMap;

/**
 * This allows us to interpret keywords better from a potentially error prone device like SMS. This class will (1) strip all non-english ascii characters, (2) reorder the word into a frequency histogram, (3) remove 1 s.
 * 
 * @author jeffrey
 */
public class EnglishKeywordNormalizer {

    public static String normalize(String valueRaw) {
        if (valueRaw == null) {
            return "";
        }
        // make lower case
        String value = valueRaw.toLowerCase();

        // build a histogram
        HashMap<Character, Integer> map = new HashMap<>();
        for (char x = 'a'; x <= 'z'; x++) {
            map.put(x, 0);
        }

        // re-order the word as a histogram
        for (char x : value.toCharArray()) {
            if (map.containsKey(x)) {
                map.put(x, map.get(x) + 1);
            }
        }

        // remove an 's' if it exists
        if (map.get('s') > 0) {
            map.put('s', map.get('s') - 1);
        }

        // make reduced thing
        StringBuilder sb = new StringBuilder();
        for (char x = 'a'; x <= 'z'; x++) {
            int v = map.get(x);
            if (v > 0) {
                sb.append(x);
                sb.append(v);
            }
        }

        // return the stream
        return sb.toString();
    }
}
