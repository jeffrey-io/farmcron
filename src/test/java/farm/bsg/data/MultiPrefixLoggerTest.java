package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.TestWorld;
import farm.bsg.TestWorld.TestLogger;

public class MultiPrefixLoggerTest {

    @Test
    public void PrefixFanning() throws Exception {
        final KeyIndex index1 = new KeyIndex("name", false);
        final KeyIndex index2 = new KeyIndex("age", true);
        final InMemoryStorage storage = new InMemoryStorage();
        final TestLogger logger = TestWorld.IN_MEMORY_PERSISTENCE_LOGGER();
        final MultiPrefixLogger prefix = new MultiPrefixLogger();
        prefix.add("1/", index1);
        prefix.add("2/", index2);

        final Value a = TestWorld.value_start().with("name", "1").with("age", "a").done();
        final Value b = TestWorld.value_start().with("name", "1").with("age", "b").done();
        final Value c = TestWorld.value_start().with("name", "3").with("age", "b").done();

        final StorageEngine proxyLogger = new StorageEngine(storage, prefix, logger);
        Assert.assertTrue(proxyLogger.put("1/a", a).success());
        Assert.assertTrue(proxyLogger.put("1/b", b).success());
        Assert.assertTrue(proxyLogger.put("1/c", c).success());

        Assert.assertTrue(proxyLogger.put("2/a", a).success());
        Assert.assertTrue(proxyLogger.put("2/b", b).success());
        Assert.assertFalse(proxyLogger.put("2/c", c).success());
    }
}
