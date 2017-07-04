package farm.bsg.data;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class InMemoryStorageTest {

    @Test
    public void getPutAndScan() {
        final InMemoryStorage storage = new InMemoryStorage();
        storage.put("x/1", new Value("\"a\""));
        storage.put("x/2", new Value("\"b\""));
        storage.put("x/z/3", new Value("\"c\""));
        storage.put("cake", new Value("\"d\""));

        Map<String, Value> map = storage.scan("x/");
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("\"a\"", map.get("x/1").toString());
        Assert.assertEquals("\"b\"", map.get("x/2").toString());
        Assert.assertEquals("\"c\"", map.get("x/z/3").toString());

        map = storage.scan("x/z/");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("\"c\"", map.get("x/z/3").toString());
    }

    @Test
    public void testGetAndPut() {
        final InMemoryStorage storage = new InMemoryStorage();
        Assert.assertEquals(null, storage.get("k"));
        storage.put("k", new Value("\"v\""));
        Assert.assertEquals("\"v\"", storage.get("k").toString());
        storage.put("k", null);
        Assert.assertEquals(null, storage.get("k"));
    }
}
