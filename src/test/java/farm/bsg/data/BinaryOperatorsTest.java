package farm.bsg.data;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

public class BinaryOperatorsTest {

    private HashSet<String> of(String... values) {
        HashSet<String> set = new HashSet<>();
        for (String value : values) {
            set.add(value);
        }
        return set;
    }

    @Test
    public void SubsetAndEqualityChecks() {
        HashSet<String> a = of("1", "2", "3");
        HashSet<String> b = of("4", "5", "6");
        HashSet<String> c = of("2", "5");
        HashSet<String> d = of("1");
        HashSet<String> e = of("4");

        Assert.assertFalse(BinaryOperators.isSubSet(c, a));
        Assert.assertFalse(BinaryOperators.isSubSet(c, b));
        Assert.assertFalse(BinaryOperators.isSubSet(b, a));
        Assert.assertFalse(BinaryOperators.isSubSet(b, a));
        Assert.assertFalse(BinaryOperators.isSubSet(d, c));
        Assert.assertFalse(BinaryOperators.isSubSet(e, c));
        Assert.assertFalse(BinaryOperators.isSubSet(d, b));
        Assert.assertFalse(BinaryOperators.isSubSet(e, a));
        Assert.assertTrue(BinaryOperators.isSubSet(d, a));
        Assert.assertTrue(BinaryOperators.isSubSet(e, b));

        Assert.assertFalse(BinaryOperators.areEqual(e, a));
        Assert.assertTrue(BinaryOperators.areEqual(a, a));
        Assert.assertTrue(BinaryOperators.areEqual(b, b));
    }

    @Test
    public void Intersection() {
        HashSet<String> a = of("1", "2", "3");
        HashSet<String> b = of("4", "5", "6");
        HashSet<String> c = of("2", "5");
        HashSet<String> d = of("1");
        HashSet<String> e = of("4");

        HashSet<String> result;

        result = BinaryOperators.intersect(a, b);
        Assert.assertEquals(0, result.size());

        result = BinaryOperators.intersect(a, e);
        Assert.assertEquals(0, result.size());

        result = BinaryOperators.intersect(a, c);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains("2"));

        result = BinaryOperators.intersect(b, c);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains("5"));

        result = BinaryOperators.intersect(a, d);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains("1"));
    }
}
