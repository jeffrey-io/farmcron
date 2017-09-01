package farm.bsg.cron;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;

import farm.bsg.ops.Logs;

/**
 * This is where all time gets managed and jobs are executed asynchronously
 *
 * @author jeffrey
 */
public class JobManager {
    private final Logger               LOG = Logs.of(JobManager.class);

    private final ArrayList<PeriodicJob> jobs;
    private boolean                    alive;
    private Thread                     lastThread;

    public JobManager() {
        this.jobs = new ArrayList<>();
        this.alive = false;
    }

    /**
     * add an hourly job to execute... hourly
     *
     * @param job
     */
    public synchronized void add(final PeriodicJob job) {
        this.jobs.add(job);
    }

    private synchronized boolean runJobs() {
        this.LOG.info("running jobs");
        for (final PeriodicJob job : this.jobs) {
            job.run(System.currentTimeMillis());
        }
        return this.alive;
    }
    
    private static long inventTime() {
        long time = 30 * 1000;
        for (int k = 0; k < 5; k++) {
            time += ThreadLocalRandom.current().nextInt(30 * 1000);
        }
        return time;
    }

    /**
     * start the job manager thread
     */
    public synchronized void start() {
        if (this.alive) {
            throw new IllegalStateException("job manager is already started");
        }
        this.alive = true;
        final CountDownLatch started = new CountDownLatch(1);
        this.lastThread = new Thread(() -> {
            started.countDown();
            while (runJobs()) {
                try {
                    long timeToSleep = inventTime();
                    JobManager.this.LOG.info("sleeping:" + timeToSleep);
                    Thread.sleep(timeToSleep);
                } catch (final InterruptedException ie) {
                }
            }
        });
        this.lastThread.setDaemon(true);
        this.lastThread.start();
        try {
            started.await();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * stop the job manager thread
     */
    public synchronized void stop() {
        this.alive = true;
        this.lastThread.interrupt();
        this.lastThread = null;
    }

}
