package farm.bsg.models;

import java.util.TreeMap;

import org.slf4j.Logger;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

import farm.bsg.BsgCounters;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.ops.Logs;

public class Habit extends RawObject {
    private final static Logger LOG = Logs.of(Habit.class);

    public static ObjectSchema SCHEMA = ObjectSchema.persisted("habits/", //
            Field.STRING("who").markAsScope(), // DONE;
            Field.STRING("last_done"), // DONE; COMPUTED

            Field.STRING("unlock_time").addProjection("edit"), // DONE; USED
            Field.STRING("warn_time").addProjection("edit"), // DONE; INPUTED; USED

            Field.STRING("name").emptyStringSameAsNull().alwaysTrim().addProjection("edit"), // DONE; USED
            Field.STRING("has_arg").addProjection("edit"), // DONE; USED
            Field.STRING("last_arg_given"), // DONE; COMPUTED
            Field.STRING("history") // DONE; COMPUTED
    );
    
    public Habit() {
        super(SCHEMA);
    }



    public TreeMap<String, String> getHistory() {
        String history = get("history");
        TreeMap<String, String> map = new TreeMap<>();
        if (history == null) {
            return map;
        }
        try {
            JsonNode tree = Jackson.jsonNodeOf(history);
            ;
            for (int k = 0; k < tree.size(); k++) {
                String[] values = tree.get(k).asText().split("\\|");
                if (values.length == 1) {
                    map.put(values[0], "yes");
                } else if (values.length == 2) {
                    map.put(values[0], values[1]);
                }
            }
        } catch (Exception err) {
            BsgCounters.I.habit_bad_history.bump();
            LOG.error("failed to parse history", err);
        }
        return map;
    }

    private HabitDerivedCached cached = null;

    public synchronized HabitDerivedCached cache(Person person) {
        if (cached == null) {
            cached = new HabitDerivedCached(person);
        } else if (!person.getId().equals(cached.person.getId())) {
            cached = new HabitDerivedCached(person);
        }
        return cached;
    }

    public class HabitDerivedCached {
        public final Person  person;
        public final boolean able;
        public final boolean warn;
        public final boolean locked;
        public final boolean done;

        private HabitDerivedCached(Person person) {
            this.person = person;
            boolean canPerform = false;
            String lastDone = get("last_done");
            if (lastDone == null) {
                canPerform = true;
            } else {
                lastDone = lastDone.trim();
                canPerform = person.getCurrentDay().compareTo(lastDone) > 0 || lastDone.equals("");
            }
            done = !canPerform;

            String unlockTime = get("unlock_time");
            if (unlockTime != null) {
                unlockTime = unlockTime.trim();
                if (unlockTime.length() > 0) {
                    int cmp = person.getCurrentHour().compareTo(unlockTime);
                    if (canPerform) {
                        canPerform = cmp >= 0;
                    }
                }
            }

            this.locked = !done && !canPerform;
            this.able = canPerform;
            boolean warning = true;
            String warnTime = get("warn_time");
            if (warnTime != null) {
                warnTime = warnTime.trim();
                if (warnTime.length() > 0) {
                    int cmp = person.getCurrentHour().compareTo(warnTime);
                    if (canPerform) {
                        warning = cmp >= 0;
                    }
                }
            }
            this.warn = warning;
        }
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Habit");
        c.counter("habit_bad_history", "Contained poorly formated history");
    }

    @Override
    protected synchronized void invalidateCache() {
        cached = null;
    }

}
