package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

public class ValueTest {

    @Test
    public void testGet() {
        Value value;
        value = new Value("{}");
        Assert.assertEquals(null, Value.getField(value, "key"));

        value = new Value("{\"key\":\"v\"}");
        Assert.assertEquals("v", Value.getField(value, "key"));

        value = new Value("{\"key\":null}");
        Assert.assertEquals(null, Value.getField(value, "key"));

    }
}
