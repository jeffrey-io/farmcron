package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;

public class TaxBaton extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("tax-baton/", //
            Field.STRING("code") // made; inserted
    );

    public TaxBaton() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }
}
