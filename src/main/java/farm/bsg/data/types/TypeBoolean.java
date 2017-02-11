package farm.bsg.data.types;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeBoolean extends Type {

    public TypeBoolean(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "boolean";
    }

    @Override
    public String normalize(String valueRaw) {
        if (valueRaw == null) {
            return "false";
        }
        String value = valueRaw.trim().toLowerCase();
        if (value.equals("true")) {
            return "true";
        }
        if (value.equals("yes")) {
            return "true";
        }
        if (value.equals("1")) {
            return "true";
        }
        if (value.equals("t")) {
            return "true";
        }
        return "false";
    }

    @Override
    public boolean validate(String valueRaw) {
        if (valueRaw == null) {
            return true; // implicit false
        }
        String value = valueRaw.trim().toLowerCase();
        if (value.equals("")) {
            return true; // implicit false
        }
        if (value.equals("true")) {
            return true; // explicit true
        }
        if (value.equals("yes")) {
            return true; // explicit true
        }
        if (value.equals("1")) {
            return true; // explicit true
        }
        if (value.equals("t")) {
            return true; // explicit true
        }
        if (value.equals("f")) {
            return true; // explicit false
        }
        if (value.equals("no")) {
            return true; // explicit false
        }
        if (value.equals("false")) {
            return true; // explicit false
        }
        return false;
    }

    @Override
    public String defaultValue() {
        return "false";
    }

    public static String project(ProjectionProvider provider, String key) {
        return provider.first(key);
    }

}