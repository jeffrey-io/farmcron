package farm.bsg.data;

import java.util.HashSet;
import java.util.Set;

public class BinaryOperators {

    public static <T> boolean areEqual(Set<T> a, Set<T> b) {
        return isSubSet(a, b) && isSubSet(b, a);
    }

    public static <T> boolean isSubSet(Set<T> a, Set<T> b) {
        for (T item : a) {
            if (!b.contains(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> HashSet<T> intersect(HashSet<T> a, HashSet<T> b) {
        HashSet<T> set = new HashSet<>();
        for (T item : a) {
            if (b.contains(item)) {
                set.add(item);
            }
        }
        return set;
    }
}
