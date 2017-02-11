package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Check extends RawObject {
    public Check() {
        super("checks/", //
                Field.STRING("ref"), // made; inserted
                Field.STRING("person").makeIndex(false), // made; copied into
                Field.DATETIME("generated"), // made
                Field.STRING("fiscal_day").makeIndex(false), // made
                Field.NUMBER("payment"), // made
                Field.STRING("ready").makeIndex(false), // made 
                Field.NUMBER("checksum") // made
        );
    }
    
    @Override
    protected void invalidateCache() {
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: Check");
    }
}
