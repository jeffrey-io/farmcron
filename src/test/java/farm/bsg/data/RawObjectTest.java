package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

public class RawObjectTest {

    @Test
    public void Coverage() {
        final ObjectSchema schema = ObjectSchema.persisted("prefix/", Field.STRING("name"));
        final RawObject o = new RawObject(schema) {
            @Override
            protected void invalidateCache() {
            }
        };
        o.generateAndSetId();
        final String id = o.get("id");
        Assert.assertNotNull(id);
        o.set("name", "fun");
        Assert.assertEquals("fun", o.get("name"));
        Assert.assertEquals("prefix/" + id, o.getStorageKey());

    }
}
