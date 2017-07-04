package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeNumber extends Type {
    private String defaultValue = "0";

    public TypeNumber(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "number";
    }

    public TypeNumber withDefault(double newDefaultValue) {
        this.defaultValue = Double.toString(newDefaultValue);
        return this;
    }

    @Override
    public boolean validate(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }

    @Override
    public String normalize(String value) {
        return value;
    }

    public static String project(ProjectionProvider provider, String key) {
        String value = provider.first(key);
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
