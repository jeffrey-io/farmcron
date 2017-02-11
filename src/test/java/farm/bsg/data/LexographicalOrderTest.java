package farm.bsg.data;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import org.junit.Assert;

public class LexographicalOrderTest {

    @Test
    public void CompareString() {
        RawObject a = new RawObject("x/", Field.STRING("key")) {
            @Override
            protected void invalidateCache() {
            }
        };
        RawObject b = new RawObject("x/", Field.STRING("key")) {
            @Override
            protected void invalidateCache() {
            }
        };
        a.set("key", "a");
        b.set("key", "b");

        ArrayList<RawObject> list = new ArrayList<>();
        list.add(b);
        list.add(a);
        Collections.sort(list, new LexographicalOrder<>(new String[] { "key" }, true, true));
        Assert.assertEquals("a", list.get(0).get("key"));
        Assert.assertEquals("b", list.get(1).get("key"));

        Collections.sort(list, new LexographicalOrder<>(new String[] { "key" }, false, true));
        Assert.assertEquals("b", list.get(0).get("key"));
        Assert.assertEquals("a", list.get(1).get("key"));
    }
}
