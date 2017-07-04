package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.TestWorld;
import farm.bsg.TestWorld.TestLogger;

public class KeyIndexTest {

    @Test
    public void IndexingUnderProxyLogger() throws Exception {
        final KeyIndex index = new KeyIndex("name", false);
        final InMemoryStorage storage = new InMemoryStorage();
        final StorageEngine proxyLogger = new StorageEngine(storage, index, TestWorld.IN_MEMORY_PERSISTENCE_LOGGER());

        proxyLogger.put("a", TestWorld.value_start().with("what", "cake").done());

        // it had nothing
        Assert.assertEquals(0, index.getIndexKeys().size());

        proxyLogger.put("a", TestWorld.value_start().with("name", "cake").done());
        Assert.assertEquals(1, index.getIndexKeys().size());
        Assert.assertTrue(index.getKeys("cake").contains("a"));

        proxyLogger.put("a", TestWorld.value_start().with("name", "munchies").done());
        Assert.assertEquals(1, index.getIndexKeys().size());
        Assert.assertTrue(index.getKeys("munchies").contains("a"));

        proxyLogger.put("a", TestWorld.value_start().done());
        Assert.assertEquals(0, index.getIndexKeys().size());
    }

    @Test
    public void IndexingUnique() throws Exception {
        final KeyIndex index = new KeyIndex("name", true);
        final InMemoryStorage storage = new InMemoryStorage();
        final TestLogger logger = TestWorld.IN_MEMORY_PERSISTENCE_LOGGER();
        final StorageEngine proxyLogger = new StorageEngine(storage, index, logger);

        Assert.assertTrue(proxyLogger.put("a", TestWorld.value_start().with("what", "cake").done()).success());
        Assert.assertEquals(0, index.getIndexKeys().size());

        Assert.assertTrue(proxyLogger.put("a", TestWorld.value_start().with("name", "cake").done()).success());
        Assert.assertEquals(1, index.getIndexKeys().size());
        Assert.assertTrue(index.getKeys("cake").contains("a"));

        Assert.assertFalse(proxyLogger.put("b", TestWorld.value_start().with("name", "cake").done()).success());
        Assert.assertEquals(1, index.getIndexKeys().size());
        Assert.assertEquals(null, storage.get("b"));

        final InMemoryStorage replay = new InMemoryStorage();
        logger.pump(replay);
        Assert.assertEquals(null, replay.get("b"));

    }
}
