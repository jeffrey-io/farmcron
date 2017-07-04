package farm.bsg.data;

import org.junit.Test;

import farm.bsg.TestWorld;

public class StorageEngineTest {
    @Test
    public void IndexingUnderProxyLogger() throws Exception {
        final KeyIndex index = new KeyIndex("name", false);
        final InMemoryStorage storage = new InMemoryStorage();
        final StorageEngine engine = new StorageEngine(storage, index, TestWorld.IN_MEMORY_PERSISTENCE_LOGGER());
        engine.put("a", TestWorld.value_start().with("what", "cake").done());
    }
}
