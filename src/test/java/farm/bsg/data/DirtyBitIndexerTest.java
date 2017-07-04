package farm.bsg.data;

import org.junit.Test;

public class DirtyBitIndexerTest {

    @Test
    public void Dedupe() {
        final MockDirtyBitIndexer indexer = new MockDirtyBitIndexer();
        for (int k = 0; k < 1000; k++) {
            indexer.put(null, null, null);
        }
        indexer.sendBegin();
        indexer.sendComplete(true);
        indexer.assertDirtyCalls(1);
    }

    @Test
    public void DedupeWithInflight() {
        final MockDirtyBitIndexer indexer = new MockDirtyBitIndexer();
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

    @Test
    public void SimpleWorkflow() {
        final MockDirtyBitIndexer indexer = new MockDirtyBitIndexer();
        indexer.put(null, null, null);
        indexer.sendBegin();
        indexer.sendComplete(true);
        indexer.assertDirtyCalls(1);
    }
}
