package farm.bsg.data;

import org.junit.Test;

public class DirtyBitIndexerTest {

    @Test
    public void SimpleWorkflow() {
        MockDirtyBitIndexer indexer = new MockDirtyBitIndexer();
        indexer.put(null, null, null);
        indexer.sendBegin();
        indexer.sendComplete(true);
        indexer.assertDirtyCalls(1);
    }

    @Test
    public void Dedupe() {
        MockDirtyBitIndexer indexer = new MockDirtyBitIndexer();
        for (int k = 0; k < 1000; k++) {
            indexer.put(null, null, null);
        }
        indexer.sendBegin();
        indexer.sendComplete(true);
        indexer.assertDirtyCalls(1);
    }

    @Test
    public void DedupeWithInflight() {
        MockDirtyBitIndexer indexer = new MockDirtyBitIndexer();
        for (int k = 0; k < 1000; k++) {
            indexer.put(null, null, null);
        }
        indexer.sendBegin();
        indexer.put(null, null, null);
        indexer.put(null, null, null);
        indexer.put(null, null, null);
        indexer.put(null, null, null);
        indexer.assertDirtyCalls(1);
        indexer.sendComplete(true);
        indexer.assertDirtyCalls(2);
        indexer.sendBegin();
        indexer.sendComplete(false);
        indexer.assertDirtyCalls(3);
        indexer.sendBegin();
        indexer.sendComplete(true);
        indexer.assertDirtyCalls(3);
    }
}
