package farm.bsg.models;

import farm.bsg.data.AsyncTaskTarget;
import farm.bsg.data.DirtyBitIndexer;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class WakeInputFile extends RawObject {

    public static ObjectSchema SCHEMA = new ObjectSchema("wake_input/", //
            Field.STRING("filename"), // -
            Field.BYTESB64("body") // -
    ).dirty("farm.bsg.models.WakeInputFile.DirtyWakeInputFile");

    public WakeInputFile() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Wake Input File");
    }
    
    public static class DirtyWakeInputFile extends DirtyBitIndexer {
        @Override
        public void onDirty(AsyncTaskTarget target) {
            target.begin();
            System.out.println("Prefix Dirty");
            target.complete(true);
        }
    }
}
