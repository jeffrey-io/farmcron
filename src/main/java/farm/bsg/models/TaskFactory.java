package farm.bsg.models;

import java.util.Set;

import org.joda.time.DateTime;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.data.types.TypeDayFilter;
import farm.bsg.data.types.TypeMonthFilter;

public class TaskFactory extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("task_factory/", //
            Field.STRING("name").addProjection("edit"), // EDIT-AVAILABLE
            Field.STRING("description").addProjection("edit"), // EDIT-AVAILABLE
            Field.STRING("current_task"), //
            Field.NUMBER("priority").withDefault(2).addProjection("edit"), // EDIT-AVAILABLE
            Field.NUMBER("frequency").addProjection("edit"), // EDIT-AVAILABLE
            Field.NUMBER("slack").addProjection("edit"), // EDIT-AVAILABLE
            Field.MONTHFILTER("month_filter").addProjection("edit"), // EDIT-AVAILABLE
            Field.DAYFILTER("day_filter").addProjection("edit") // EDIT-AVAILABLE
    );

    public TaskFactory() {
        super(SCHEMA);
    }
    
    @Override
    protected void invalidateCache() {
    }
    
    public boolean ready(long now) {
        DateTime consideration = new DateTime(now);
        Set<Integer> monthsAvailale = TypeMonthFilter.ordinalsOf(get("month_filter"));
        if (monthsAvailale.contains(consideration.getMonthOfYear())) {
            Set<Integer> daysAvailable = TypeDayFilter.ordinalsOf(get("day_filter"));
            return daysAvailable.contains(consideration.getDayOfWeek());
        }
        return false;
    }
}
