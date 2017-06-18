package farm.bsg.models;

import java.util.concurrent.TimeUnit;

import farm.bsg.QueryEngine;
import farm.bsg.data.AsyncTaskTarget;
import farm.bsg.data.DirtyBitIndexer;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class WakeInputFile extends RawObject {

    public static ObjectSchema SCHEMA = ObjectSchema.persisted("wake_input/", //
            Field.STRING("filename").makeIndex(true), // -
            Field.STRING("content_type"), // -
            Field.STRING("description"), // -
            Field.BYTESB64("contents") // -
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
        private QueryEngine engine;
        
        public DirtyWakeInputFile(QueryEngine engine) {
            this.engine = engine;
        }

        @Override
        public void onDirty(AsyncTaskTarget target) {
            engine.scheduler.schedule(() -> {
                AsyncTaskTarget.execute(engine.executor, target, () -> {
                    System.out.println("Wake files being processed async!");
                });
            }, 500, TimeUnit.MILLISECONDS);
        }
    }
}
