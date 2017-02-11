package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeDateTime extends Type {
    public TypeDateTime(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "datetime";
    }

    @Override
    public String normalize(String value) {
        return value;
    }

    @Override
    public boolean validate(String value) {
        return true;
    }

    @Override
    public String defaultValue() {
        return null;
    }

    public static String project(ProjectionProvider provider, String key) {
        return provider.first(key);
    }
}
