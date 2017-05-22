package farm.bsg.models;

import farm.bsg.data.RawObject;
import farm.bsg.data.types.TypeDayFilter;
import farm.bsg.data.types.TypeMonthFilter;
import farm.bsg.ops.CounterCodeGen;
import java.util.Comparator;
import java.util.Set;

import org.joda.time.DateTime;
import farm.bsg.data.BinaryOperators;
import farm.bsg.data.Field;

public class Chore extends RawObject {

    private DateTime now;

    public Chore() {
        super("chore/", //
                Field.STRING("name").addProjection("edit"), // USED
                Field.STRING("last_performed"), // COMPUTED; USED
                Field.STRING("last_performed_by"), // COMPUTED

                Field.STRING("frequency").addProjection("edit"), // i.e. 7 day since last performed; USED
                Field.STRING("slack").addProjection("edit"), // i.e. 2 days; USED

                Field.MONTHFILTER("month_filter").addProjection("edit"), // HAS INPUT;
                Field.DAYFILTER("day_filter").addProjection("edit"), // HAS INPUT;

                Field.STRING("time_to_perform_hours").addProjection("edit"), // HAS_INPUT;

                Field.STRING("equipment_skills_required").addProjection("edit"), // USED, crap input
                Field.STRING("weather_requirements").addProjection("edit"), // NOT USED
                Field.STRING("hour_filter").addProjection("edit"), // "", 1000|1700

                Field.STRING("manual").addProjection("edit") // HAS INPUT
        );
        now = new DateTime();
    }

    public void setNow(DateTime now) {
        this.now = now;
    }

    @Override
    protected synchronized void invalidateCache() {
        cache = null;
    }

    private Cache cache;

    private class Cache {
        final DateTime due;
        final boolean  late;
        final boolean  ready;
        final int      daysAvailable;
        final DateTime firstAvailableDay;
        final boolean  complete;
        final int      timesLeftPerYear;
        final int[]    timesByDay;
        final boolean  isTodayGood;

        Cache() {
            // extract the frequency, slack, and last_performed
            int freq = getAsInt("frequency");
            int slack = getAsInt("slack");
            DateTime lastPerformed = getTimestamp("last_performed");
            Set<Integer> monthsAvailale = TypeMonthFilter.ordinalsOf(get("month_filter"));
            Set<Integer> daysAvailable = TypeDayFilter.ordinalsOf(get("day_filter"));
            if (lastPerformed == null) {
                // it has never been done, so let's assume it was done on schedule last time
                lastPerformed = now.minusDays(freq + 1);
            }

            int[] timeByDay = new int[7];
            for (int k = 0; k < timeByDay.length; k++) {
                timeByDay[k] = 0;
            }

            // The ideal time for it to be due
            DateTime idealDay = lastPerformed.plusDays(freq);
            DateTime startWalkingDay = idealDay.minusDays(slack);
            boolean maybeLate = false;
            if (idealDay.compareTo(now) < 0) {
                // we are overdue, so today is as good as we can get.
                idealDay = now;
                maybeLate = true;
            }
            // make sure we don't consider days in the past
            if (startWalkingDay.compareTo(now) <= 0) {
                startWalkingDay = now;
            }

            DateTime firstAvailableDay = null;
            int daysAvailableUntilLate = 0;

            DateTime lastDone = null;
            int timesPerformedInFuture = 0;

            for (int dayPlus = 0; dayPlus < 365; dayPlus++) {
                DateTime consideration = startWalkingDay.plusDays(dayPlus);
                boolean sameYear = consideration.getYear() == now.getYear();
                if (!sameYear) {
                    // only consider days within the year
                    break;
                }

                boolean available = monthsAvailale.contains(consideration.getMonthOfYear());

                if (consideration.compareTo(idealDay) < 0 && available) {
                    daysAvailableUntilLate++;
                }

                if (lastDone != null && available) {
                    // we have done it once, and today is available
                    if (lastDone.compareTo(consideration) <= 0) {
                        // the time we should have last done it is in the past, so let's do it now while it is available
                        lastDone = lastDone.plusDays(freq);
                        timesPerformedInFuture++;
                        timeByDay[consideration.getDayOfWeek() - 1]++;
                    }
                }

                if (firstAvailableDay == null && available) {
                    firstAvailableDay = consideration;
                    timesPerformedInFuture = 1;
                    timeByDay[consideration.getDayOfWeek() - 1]++;
                    lastDone = firstAvailableDay.plusDays(freq);
                }
            }

            this.timesLeftPerYear = timesPerformedInFuture;
            if (firstAvailableDay == null) {
                firstAvailableDay = startWalkingDay.plusDays(365);
            }
            this.timesByDay = timeByDay;
            this.due = idealDay;
            this.daysAvailable = daysAvailableUntilLate;
            this.firstAvailableDay = firstAvailableDay;
            this.ready = firstAvailableDay.compareTo(now) <= 0;
            this.isTodayGood = this.ready && daysAvailable.contains(now.getDayOfWeek());
            this.late = this.ready && maybeLate;
            this.complete = getAsDouble("time_to_perform_hours") > 0.0001;
        }
    }

    private synchronized Cache cache() {
        if (cache == null) {
            cache = new Cache();
        }
        return cache;
    }

    public String dayDue() {
        return cache().due.toString("yyyyMMdd");
    }

    public int daysAvailable() {
        return cache().daysAvailable;
    }

    public boolean late() {
        return cache().late;
    }
    
    public int[] futureByDay() {
        return cache().timesByDay;
    }
    
    public boolean isTodayGood() {
        return cache().isTodayGood;
    }


    public boolean ready() {
        return cache().ready;
    }

    public boolean complete() {
        return cache().complete;
    }

    public int future() {
        return cache().timesLeftPerYear;
    }

    public String firstAvailableDay() {
        DateTime first = cache().firstAvailableDay;
        if (first == null) {
            return "never";
        }
        return first.toString("yyyyMMdd");
    }

    public static class Ranking implements Comparator<Chore> {
        @Override
        public int compare(Chore o1, Chore o2) {
            int diff = o1.firstAvailableDay().compareTo(o2.firstAvailableDay());
            if (diff != 0) {
                return diff;
            }
            diff = o1.daysAvailable() - o2.daysAvailable();
            if (diff != 0) {
                return diff;
            }
            diff = o1.dayDue().compareTo(o2.dayDue());
            return diff;
        }
    }

    public boolean canBeDoneBy(Person person) {
        Set<String> skillsRequired = getTokenList("equipment_skills_required");
        Set<String> skillsAvailable = person.getTokenList("equipment_skills");
        return BinaryOperators.isSubSet(skillsRequired, skillsAvailable);
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Chore");
    }

}
