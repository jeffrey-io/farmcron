package farm.bsg.data;

import java.util.Comparator;

public class LexographicalOrder<T extends RawObject> implements Comparator<T> {

    private final String[] keys;
    private boolean        asc;
    private boolean        caseSensitive = true;

    public LexographicalOrder(String[] keys, boolean asc, boolean caseSensitive) {
        this.keys = keys;
        this.asc = asc;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public int compare(T a, T b) {
        for (String key : keys) {
            int result = Value.stringCompareWithNullChecks(a.get(key), b.get(key), caseSensitive);
            if (result == 0) {
                continue;
            }
            if (asc) {
                return result;
            } else {
                return -result;
            }
        }
        return 0;
    }

}