package farm.bsg.data;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class LexographicalOrderTest {

    @Test
    public void CompareString() {
        final ObjectSchema schema = ObjectSchema.persisted("x/", Field.STRING("key"));
        final RawObject a = new RawObject(schema) {
            @Override
            protected void invalidateCache() {
            }
        };
        final RawObject b = new RawObject(schema) {
            @Override
            protected void invalidateCache() {
            }
        };
        a.set("key", "a");
        b.set("key", "b");

        final ArrayList<RawObject> list = new ArrayList<>();
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
