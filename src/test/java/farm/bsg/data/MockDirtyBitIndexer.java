package farm.bsg.data;

import org.junit.Assert;

public class MockDirtyBitIndexer extends DirtyBitIndexer {

    public AsyncTaskTarget currentTarget;
    private int            dirtyCalls;

    public MockDirtyBitIndexer() {
        currentTarget = null;
        dirtyCalls = 0;
    }

    @Override
    public synchronized void onDirty(AsyncTaskTarget target) {
        Assert.assertNull(currentTarget);
        this.currentTarget = target;
        dirtyCalls++;
    }

    public void assertDirtyCalls(int expected) {
        Assert.assertEquals(expected, dirtyCalls);
    }

    public synchronized void sendBegin() {
        Assert.assertNotNull(currentTarget);
        currentTarget.begin();
    }

    public synchronized void sendComplete(boolean success) {
        AsyncTaskTarget old = currentTarget;
        currentTarget = null;
        old.complete(success);
    }
}
