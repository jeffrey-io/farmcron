package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class WakeInputFile extends RawObject {
    public WakeInputFile() {
        super("wake_input/", //
                Field.STRING("filename"), // -
                Field.BYTESB64("body") // -
                );
    }

    @Override
    protected void invalidateCache() {
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: Wake Input File");
    }
}
