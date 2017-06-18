package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Task extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("task/", //
            Field.STRING("owner").makeIndex(false),
            
            Field.STRING("name"), //
            Field.STRING("description"), //
            Field.NUMBER("priority").withDefault(3), //
            Field.DATETIME("due"), //

            Field.DATETIME("created"), // 
            Field.DATETIME("started"), // 
            Field.DATETIME("closed"), //

            Field.STRING("state").alwaysTrim().emptyStringSameAsNull().makeIndex(false) // 
    );

    public Task() {
        super(SCHEMA);
    }
    
    @Override
    protected void invalidateCache() {
    }
    
    public void setState(String state) {
        set(state, isoTimestamp());
        set("state", state);
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Tasks");
    }
    
    public boolean canStart() {
        String state = get("state");
        return "created".equals(state);
    }
    
    public boolean canClose() {
        String state = get("state");
        return "created".equals(state) || "started".equals(state);
    }
    
    public static boolean isClosedAndReadyForTransition(Task task, long now) {
        if (task == null) {
            return true;
        }
        return task.isClosedAndReadyForTransition(now);
    }

    public boolean isClosedAndReadyForTransition(long now) {
        return false;
    }
}
