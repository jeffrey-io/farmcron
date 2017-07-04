package farm.bsg.cron;

import org.junit.Test;

public class JobManagerTest {

    @Test
    public void HourlyRunFirst() {
        final JobManager mgr = new JobManager();
        final MockHourlyJob job = new MockHourlyJob();
        mgr.add(job);
        mgr.start();
        job.assertIsCalled();
        mgr.stop();
    }

    @Test
    public void StartStop() {
        final JobManager mgr = new JobManager();
        mgr.start();
        mgr.stop();
    }
}
