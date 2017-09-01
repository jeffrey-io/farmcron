package farm.bsg.cron;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public class MockHourlyJob implements PeriodicJob {

    private final CountDownLatch latch;

    public MockHourlyJob() {
        this.latch = new CountDownLatch(1);
    }

    public void assertIsCalled() {
        try {
            Assert.assertTrue(this.latch.await(60000, TimeUnit.MILLISECONDS));
        } catch (final InterruptedException ie) {
            Assert.fail();
        }
    }

    @Override
    public void run(final long now) {
        this.latch.countDown();
    }

}
