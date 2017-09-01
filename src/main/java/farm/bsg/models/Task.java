package farm.bsg.models;

import org.joda.time.DateTime;

import farm.bsg.BsgCounters;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Task extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("task/", //
            Field.STRING("owner").makeIndex(false),

            Field.STRING("blocked_by"), // a task that will block the rendering of this task.

            Field.STRING("cart_id"), // for when tasks are created by an order

            Field.STRING("name"), //
            Field.STRING("description"), //
            Field.NUMBER("priority").withDefault(3), //
            Field.DATETIME("due_date"), //

            Field.NUMBER("snooze_time").withDefault(720), // EDIT-AVAILABLE

            Field.DATETIME("created"), //
            Field.DATETIME("snoozed"), //
            Field.DATETIME("closed"), //

            Field.STRING("notification_token_for_closed"), //
            Field.STRING("notification_short_text_for_closed"), //

            Field.STRING("state").alwaysTrim().emptyStringSameAsNull().makeIndex(false) //
    );

    public static boolean isClosedAndReadyForTransition(final Task task, final long now, final int daysAfter) {
        if (task == null) {
            return true;
        }
        return task.isClosedAndReadyForTransition(now, daysAfter);
    }

    public static void link(final CounterCodeGen c) {
        c.section("Data: Tasks");
        c.counter("task_transition", "a task was transitioned to a new state");
        c.counter("task_snooze", "a task was put to sleep");
        c.counter("task_woke", "a task was brought back from sleep");
        c.counter("task_close", "a task was close");
    }

    public Task() {
        super(SCHEMA);
    }

    public boolean canTransition() {
        return "created".equals(get("state"));
    }

    public boolean close() {
        if (setInternalState("closed", true)) {
            BsgCounters.I.task_close.bump();
            return true;
        }
        return false;
    }

    public boolean created() {
        return setInternalState("created", true);
    }

    @Override
    protected void invalidateCache() {
    }

    public boolean isClosedAndReadyForTransition(final long now, final int daysAfter) {
        final String state = get("state");
        if (!"closed".equals(state)) {
            return false;
        }
        final DateTime lastPerformed = getTimestamp("closed").withMillisOfDay(0).withHourOfDay(7);
        final DateTime today = new DateTime(now);
        final DateTime dayAfterPerformed = lastPerformed.minusMillis(lastPerformed.getMillisOfDay()).plusDays(daysAfter);
        return today.isAfter(dayAfterPerformed);
    }

    public boolean ready() {
        return isoTimestamp().compareTo(readyIsoTime()) >= 0;
    }

    public String readyIsoTime() {
        final long snoozeTime = getTimestamp("snoozed").plusMinutes(getAsInt("snooze_time")).getMillis();
        return isoTimestamp(snoozeTime);
    }

    public void setDue(final long now, final int daysDue) {
        DateTime dueDate = new DateTime(now).withMillisOfDay(0).withHourOfDay(17).plusDays(daysDue + 1);
        dueDate = dueDate.minusMillis(dueDate.getMillisOfDay());
        set("due_date", RawObject.isoTimestamp(dueDate.getMillis()));
    }

    private boolean setInternalState(final String state, final boolean recordTime) {
        final String prior = get("state");
        if (state.equals(prior)) {
            // no-op
            return false;
        } else {
            if (recordTime) {
                set(state, isoTimestamp());
            }
            set("state", state);
            BsgCounters.I.task_transition.bump();
            return true;
        }
    }

    public boolean snooze() {
        if (setInternalState("snoozed", true)) {
            BsgCounters.I.task_snooze.bump();
            // set sleeping until
            return true;
        }
        return false;
    }

    public void wake() {
        if (setInternalState("created", false)) {
            BsgCounters.I.task_woke.bump();
        }
    }
}
