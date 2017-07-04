package farm.bsg.data.types;

import java.util.ArrayList;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeStringTokenList extends Type {
    public static String project(final ProjectionProvider provider, final String key) {
        final String[] values = provider.multiple(key);
        if (values == null) {
            return null;
        }
        final ArrayList<String> list = new ArrayList<>();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            value = value.trim().toLowerCase();
            if (value.length() > 0) {
                list.add(value);
            }
        }
        return String.join(",", list);
    }

    public TypeStringTokenList(final String name) {
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
        return "token_string_list";
    }

    @Override
    public boolean validate(final String value) {
        return true;
    }
}
