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

            Field.NUMBER("cart_id"), // for when tasks are created by an order

            Field.STRING("name"), //
            Field.STRING("description"), //
            Field.NUMBER("priority").withDefault(3), //
            Field.DATETIME("due_date"), //

            Field.DATETIME("created"), //
            Field.DATETIME("started"), //
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
    }

    public Task() {
        super(SCHEMA);
    }

    public boolean canClose() {
        final String state = get("state");
        return "created".equals(state) || "started".equals(state);
    }

    public boolean canStart() {
        final String state = get("state");
        return "created".equals(state);
    }

    @Override
    protected void invalidateCache() {
    }

    public boolean isClosedAndReadyForTransition(final long now, final int daysAfter) {
        final String state = get("state");
        if (!"closed".equals(state)) {
            return false;
        }
        final DateTime lastPerformed = getTimestamp("closed");
        final DateTime today = new DateTime(now);
        final DateTime dayAfterPerformed = lastPerformed.minusMillis(lastPerformed.getMillisOfDay()).plusDays(daysAfter);
        return today.isAfter(dayAfterPerformed);
    }

    public void setDue(final long now, final int daysDue) {
        DateTime dueDate = new DateTime(now).plusDays(daysDue + 1);
        dueDate = dueDate.minusMillis(dueDate.getMillisOfDay());
        set("due_date", RawObject.isoTimestamp(dueDate.getMillis()));
    }

    public void setState(final String state) {
        final String prior = get("state");
        if (state.equals(prior)) {

        } else {
            set(state, isoTimestamp());
            set("state", state);
            BsgCounters.I.task_transition.bump();
        }
    }
}
