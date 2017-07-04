package farm.bsg.data;

import java.util.Comparator;

public class LexographicalOrder<T extends RawObject> implements Comparator<T> {

    private final String[] keys;
    private final boolean  asc;
    private boolean        caseSensitive = true;

    public LexographicalOrder(final String[] keys, final boolean asc, final boolean caseSensitive) {
        this.keys = keys;
        this.asc = asc;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public int compare(final T a, final T b) {
        for (final String key : this.keys) {
            final int result = Value.stringCompareWithNullChecks(a.get(key), b.get(key), this.caseSensitive);
            if (result == 0) {
                continue;
            }
            if (this.asc) {
                return result;
            } else {
                return -result;
            }
        }
        return 0;
    }

}