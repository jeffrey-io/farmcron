package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeUUID extends Type {

    public static String project(final ProjectionProvider provider, final String key) {
        final String value = provider.first(key);
        if (value == null) {
            return value;
        }
        return value.trim();
    }

    public TypeUUID(final String name) {
        super(name);
    }

    @Override
    public String defaultValue() {
        return null;
    }

    @Override
    public String normalize(final String value) {
        return value;
    }

    @Override
    public String type() {
        return "uuid";
    }

    @Override
    public boolean validate(final String value) {
        return true;
    }
}
