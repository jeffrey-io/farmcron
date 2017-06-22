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
        complete_called = 0;
        lastSuccess = false;
    }

    @Override
    public synchronized void begin() {
        begin_called++;
    }

    @Override
    public synchronized void complete(boolean success) {
        complete_called++;
        lastSuccess = success;
        if (completeLatch != null) {
            completeLatch.countDown();
            completeLatch = null;
        }
    }
    
    public void assertLastSuccess(boolean expected) {
        Assert.assertEquals(expected, lastSuccess);
    }

    public synchronized void installCompleteLatch() {
        if (completeLatch == null) {
            completeLatch = new CountDownLatch(1);
            return;
        }
        Assert.fail();
    }

    private synchronized int getBegin() {
        return begin_called;
    }

    public void pollBegin(int expected) {
        try {
            int timeout = 1000;
            while (expected != getBegin() && timeout > 0) {
                timeout--;
                Thread.sleep(5);
            }
            Assert.assertEquals(expected, getBegin());
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }

    public synchronized void assertStatus(int begin, int complete) {
        Assert.assertEquals(begin, begin_called);
        Assert.assertEquals(complete, complete_called);
    }

    private synchronized void removeCompleteLatch() {
        completeLatch = null;
    }

    public void waitForCompleteLatch() {
        try {
            Assert.assertTrue(completeLatch.await(60000, TimeUnit.MILLISECONDS));
            removeCompleteLatch();
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }

}
