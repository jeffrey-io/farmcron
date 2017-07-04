package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeNumber extends Type {
    public static String project(final ProjectionProvider provider, final String key) {
        final String value = provider.first(key);
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private String defaultValue = "0";

    public TypeNumber(final String name) {
        super(name);
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }

    @Override
    public String normalize(final String value) {
        return value;
    }

    @Override
    public String type() {
        return "number";
    }

    @Override
    public boolean validate(final String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    public TypeNumber withDefault(final double newDefaultValue) {
        this.defaultValue = Double.toString(newDefaultValue);
        return this;
    }
}
