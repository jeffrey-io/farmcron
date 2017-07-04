package farm.bsg.data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class AsyncTaskTargetTest {

    @Test
    public void validateExecutorSideStep() {
        final CountDownLatch gate = new CountDownLatch(1);
        final MockAsyncTaskTarget target = new MockAsyncTaskTarget();
        final ExecutorService executor = Executors.newCachedThreadPool();
        AsyncTaskTarget.execute(executor, target, () -> {
            try {
                gate.await(60000, TimeUnit.MILLISECONDS);
                return true;
            } catch (final InterruptedException ie) {
                Assert.fail();
                return false;
            }
        });
        target.pollBegin(1);
        target.installCompleteLatch();
        gate.countDown();
        target.waitForCompleteLatch();
        target.assertStatus(1, 1);

    }
}
