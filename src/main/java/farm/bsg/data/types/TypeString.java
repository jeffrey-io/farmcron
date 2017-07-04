package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeString extends Type {

    public static String project(final ProjectionProvider provider, final String key) {
        return provider.first(key);
    }

    private String  defaultValue;
    private boolean emptyIsNull = false;

    private boolean alwaysTrim  = false;

    public TypeString(final String name) {
        super(name);
        this.defaultValue = null;
    }

    public TypeString alwaysTrim() {
        this.alwaysTrim = true;
        return this;
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }

    public TypeString emptyStringSameAsNull() {
        this.emptyIsNull = true;
        return this;
    }

    @Override
    public String normalize(final String valueRaw) {
        String value = valueRaw;
        if (value == null) {
            return value;
        }
        if (this.alwaysTrim) {
            value = value.trim();
        }
        if (this.emptyIsNull && "".equals(value)) {
            return null;
        }
        return value;
    }

    @Override
    public String type() {
        return "string";
    }

    @Override
    public boolean validate(final String value) {
        return true;
    }

    public TypeString withDefault(final String newDefaultValue) {
        this.defaultValue = newDefaultValue;
        return this;
    }
}
