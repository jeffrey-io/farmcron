package farm.bsg.data;

import java.util.HashSet;
import java.util.Set;

public class BinaryOperators {

    public static <T> boolean areEqual(final Set<T> a, final Set<T> b) {
        return isSubSet(a, b) && isSubSet(b, a);
    }

    public static <T> HashSet<T> intersect(final HashSet<T> a, final HashSet<T> b) {
        final HashSet<T> set = new HashSet<>();
        for (final T item : a) {
            if (b.contains(item)) {
                set.add(item);
            }
        }
        return set;
    }

    public static <T> boolean isSubSet(final Set<T> a, final Set<T> b) {
        for (final T item : a) {
            if (!b.contains(item)) {
                return false;
            }
        }
        return true;
    }
}
