package farm.bsg.cron;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public class MockHourlyJob implements HourlyJob {

    private final CountDownLatch latch;

    public MockHourlyJob() {
        latch = new CountDownLatch(1);
    }

    @Override
    public void run(long now) {
        latch.countDown();
    }

    public void assertIsCalled() {
        try {
            Assert.assertTrue(latch.await(60000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException ie) {
            Assert.fail();
        }
    }

}
