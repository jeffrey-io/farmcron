package farm.bsg.data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public class MockAsyncTaskTarget implements AsyncTaskTarget {

    private int            begin_called;
    private int            complete_called;
    private boolean        lastSuccess   = false;

    private CountDownLatch completeLatch = null;

    public MockAsyncTaskTarget() {
        this.begin_called = 0;
        this.complete_called = 0;
        this.lastSuccess = false;
    }

    public void assertLastSuccess(final boolean expected) {
        Assert.assertEquals(expected, this.lastSuccess);
    }

    public synchronized void assertStatus(final int begin, final int complete) {
        Assert.assertEquals(begin, this.begin_called);
        Assert.assertEquals(complete, this.complete_called);
    }

    @Override
    public synchronized void begin() {
        this.begin_called++;
    }

    @Override
    public synchronized void complete(final boolean success) {
        this.complete_called++;
        this.lastSuccess = success;
        if (this.completeLatch != null) {
            this.completeLatch.countDown();
            this.completeLatch = null;
        }
    }

    private synchronized int getBegin() {
        return this.begin_called;
    }

    public synchronized void installCompleteLatch() {
        if (this.completeLatch == null) {
            this.completeLatch = new CountDownLatch(1);
            return;
        }
        Assert.fail();
    }

    public void pollBegin(final int expected) {
        try {
            int timeout = 1000;
            while (expected != getBegin() && timeout > 0) {
                timeout--;
                Thread.sleep(5);
            }
            Assert.assertEquals(expected, getBegin());
        } catch (final InterruptedException ie) {
            Assert.fail();
        }
    }

    private synchronized void removeCompleteLatch() {
        this.completeLatch = null;
    }

    public void waitForCompleteLatch() {
        try {
            Assert.assertTrue(this.completeLatch.await(60000, TimeUnit.MILLISECONDS));
            removeCompleteLatch();
        } catch (final InterruptedException ie) {
            Assert.fail();
        }
    }

}
