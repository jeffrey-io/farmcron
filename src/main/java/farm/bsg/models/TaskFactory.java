package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;

public class TaskFactory extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("task_factory/", //
            Field.STRING("name").addProjection("edit"), // EDIT-AVAILABLE
            Field.STRING("description").addProjection("edit"), // EDIT-AVAILABLE
            Field.STRING("current_task"), //

            Field.NUMBER("priority").addProjection("edit"), // EDIT-AVAILABLE
            Field.NUMBER("frequency").addProjection("edit"), // EDIT-AVAILABLE
            Field.NUMBER("slack").addProjection("edit"), // EDIT-AVAILABLE

            Field.MONTHFILTER("month_filter").addProjection("edit"), // EDIT-AVAILABLE
            Field.DAYFILTER("day_filter").addProjection("edit") // EDIT-AVAILABLE
    );

    public TaskFactory() {
        super(SCHEMA);
    }
    
    public boolean ready(Task task, long now) {
        if (Task.isClosedAndReadyForTransition(task, now)) {

            // check to see if today is good.
        }
        return false;
    }

    @Override
    protected void invalidateCache() {
    }
}
