package farm.bsg.cron;

/**
 * A job that runs hourly
 *
 * @author jeffrey
 */
@FunctionalInterface
public interface PeriodicJob {
    
    void run(long now);
}
