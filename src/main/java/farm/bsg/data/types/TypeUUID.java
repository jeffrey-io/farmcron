package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeUUID extends Type {

    public TypeUUID(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "uuid";
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
        String value = provider.first(key);
        if (value == null) {
            return value;
        }
        return value.trim();
    }
}
