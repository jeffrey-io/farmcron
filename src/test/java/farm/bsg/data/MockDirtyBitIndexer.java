package farm.bsg.data;

import org.junit.Assert;

public class MockDirtyBitIndexer extends DirtyBitIndexer {

    public AsyncTaskTarget currentTarget;
    private int            dirtyCalls;

    public MockDirtyBitIndexer() {
        this.currentTarget = null;
        this.dirtyCalls = 0;
    }

    public void assertDirtyCalls(final int expected) {
        Assert.assertEquals(expected, this.dirtyCalls);
    }

    @Override
    public synchronized void onDirty(final AsyncTaskTarget target) {
        Assert.assertNull(this.currentTarget);
        this.currentTarget = target;
        this.dirtyCalls++;
    }

    public synchronized void sendBegin() {
        Assert.assertNotNull(this.currentTarget);
        this.currentTarget.begin();
    }

    public synchronized void sendComplete(final boolean success) {
        final AsyncTaskTarget old = this.currentTarget;
        this.currentTarget = null;
        old.complete(success);
    }
}
