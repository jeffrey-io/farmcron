package farm.bsg.cron;

import org.junit.Test;

public class JobManagerTest {

    @Test
    public void StartStop() {
        JobManager mgr = new JobManager();
        mgr.start();
        mgr.stop();
    }

    @Test
    public void HourlyRunFirst() {
        JobManager mgr = new JobManager();
        MockHourlyJob job = new MockHourlyJob();
        mgr.add(job);
        mgr.start();
        job.assertIsCalled();
        mgr.stop();
    }
}
