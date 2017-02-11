package farm.bsg.data;

import org.junit.Test;

import farm.bsg.TestWorld;
import farm.bsg.TestWorld.TestLogger;
import org.junit.Assert;

public class MultiPrefixLoggerTest {

    @Test
    public void PrefixFanning() throws Exception {
        KeyIndex index1 = new KeyIndex("name", false);
        KeyIndex index2 = new KeyIndex("age", true);
        InMemoryStorage storage = new InMemoryStorage();
        TestLogger logger = TestWorld.IN_MEMORY_PERSISTENCE_LOGGER();
        MultiPrefixLogger prefix = new MultiPrefixLogger();
        prefix.add("1/", index1);
        prefix.add("2/", index2);

        Value a = TestWorld.value_start().with("name", "1").with("age", "a").done();
        Value b = TestWorld.value_start().with("name", "1").with("age", "b").done();
        Value c = TestWorld.value_start().with("name", "3").with("age", "b").done();

        StorageEngine proxyLogger = new StorageEngine(storage, prefix, logger);
        Assert.assertTrue(proxyLogger.put("1/a", a).success());
        Assert.assertTrue(proxyLogger.put("1/b", b).success());
        Assert.assertTrue(proxyLogger.put("1/c", c).success());

        Assert.assertTrue(proxyLogger.put("2/a", a).success());
        Assert.assertTrue(proxyLogger.put("2/b", b).success());
        Assert.assertFalse(proxyLogger.put("2/c", c).success());
    }
}
