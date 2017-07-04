package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeDateTime extends Type {
    public static String project(final ProjectionProvider provider, final String key) {
        return provider.first(key);
    }

    public TypeDateTime(final String name) {
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
        return "datetime";
    }

    @Override
    public boolean validate(final String value) {
        return true;
    }
}
