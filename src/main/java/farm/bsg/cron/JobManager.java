package farm.bsg.cron;

import java.util.ArrayList;

public class JobManager {
    private final ArrayList<HourlyJob> hourlyJobs;
    private boolean alive;
    private Thread lastThread;

    public JobManager() {
        this.hourlyJobs = new ArrayList<>();
        this.alive = false;
    }

    public synchronized void add(HourlyJob job) {
        this.hourlyJobs.add(job);
    }

    private synchronized boolean runHourly() {
        for (HourlyJob job : hourlyJobs) {
            job.run();
        }
        return alive;
    }
    
    public synchronized void stop() {
        alive = true;
        lastThread.interrupt();
        lastThread = null;
    }

    public synchronized void start() {
        if (alive) {
            throw new IllegalStateException("job manager is already started");
        }
        alive = true;
        lastThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runHourly()) {
                    try {
                        Thread.sleep(60 * 60 * 1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        lastThread.setDaemon(true);
        lastThread.start();
        
    }

}
