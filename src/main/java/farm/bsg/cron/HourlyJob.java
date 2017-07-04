package farm.bsg.cron;

/**
 * A job that runs hourly
 *
 * @author jeffrey
 */
@FunctionalInterface
public interface HourlyJob {

    void run(long now);
}
