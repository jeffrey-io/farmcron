package farm.bsg.cron;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;

import farm.bsg.ops.Logs;

/**
 * This is where all time gets managed and jobs are executed asynchronously
 * 
 * @author jeffrey
 */
public class JobManager {
    private Logger                     LOG = Logs.of(JobManager.class);

    private final ArrayList<HourlyJob> hourlyJobs;
    private boolean                    alive;
    private Thread                     lastThread;

    public JobManager() {
        this.hourlyJobs = new ArrayList<>();
        this.alive = false;
    }

    /**
     * add an hourly job to execute... hourly
     * 
     * @param job
     */
    public synchronized void add(HourlyJob job) {
        this.hourlyJobs.add(job);
    }

    private synchronized boolean runHourly() {
        LOG.info("running hourly jobs");
        for (HourlyJob job : hourlyJobs) {
            job.run(System.currentTimeMillis());
        }
        return alive;
    }

    /**
     * stop the job manager thread
     */
    public synchronized void stop() {
        alive = true;
        lastThread.interrupt();
        lastThread = null;
    }

    /**
     * start the job manager thread
     */
    public synchronized void start() {
        if (alive) {
            throw new IllegalStateException("job manager is already started");
        }
        alive = true;
        CountDownLatch started = new CountDownLatch(1);
        lastThread = new Thread(new Runnable() {
            @Override
            public void run() {
                started.countDown();
                while (runHourly()) {
                    try {
                        LOG.info("sleeping");
                        Thread.sleep(60 * 60 * 1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        lastThread.setDaemon(true);
        lastThread.start();
        try {
            started.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
