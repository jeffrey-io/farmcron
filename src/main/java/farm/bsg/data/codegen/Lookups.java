package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;

public class Lookups {
    public static void write(ArrayList<String> lines, String name, ObjectSchema object) {
        lines.add("");
        lines.add("  public " + name + " " + name.toLowerCase() + "_by_id(String id, boolean create) {");
        lines.add("    Value v = storage.get(\"" + object.getPrefix() + "\" + id);");
        lines.add("    if (v == null && !create) {");
        lines.add("      return null;");
        lines.add("    }");
        lines.add("    " + name + " result = " + name.toLowerCase() + "_of(v);");
        lines.add("    result.set(\"id\", id);");
        lines.add("    return result;");
        lines.add("  }");
    }

}
