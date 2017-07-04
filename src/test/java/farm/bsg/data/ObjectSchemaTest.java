package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

public class ObjectSchemaTest {

    @Test
    public void Coverage() {
        ObjectSchema schema = ObjectSchema.persisted("prefix/", Field.STRING("name"));
        Assert.assertEquals("name", schema.get("name").name());
        Assert.assertEquals(3, schema.getTypes().size()); // id, ref, +name
        boolean failed = false;
        try {
            schema.getTypes().add(Field.BOOL("nope"));
            failed = true;
        } catch (Exception err) {

        }
        Assert.assertFalse(failed);
    }
}
