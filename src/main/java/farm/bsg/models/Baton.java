package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;

public class Baton extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("baton/", //
            Field.STRING("code") // made; inserted
    );

    public Baton() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }
}
