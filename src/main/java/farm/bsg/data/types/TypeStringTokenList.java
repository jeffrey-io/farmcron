package farm.bsg.data.types;

import java.util.ArrayList;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeStringTokenList extends Type {
    public TypeStringTokenList(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "token_string_list";
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
        String[] values = provider.multiple(key);
        if (values == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
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
}
