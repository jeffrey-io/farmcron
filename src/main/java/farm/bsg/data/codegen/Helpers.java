package farm.bsg.data.codegen;

import java.util.ArrayList;

public class Helpers {
    public static void write(ArrayList<String> lines) {
        lines.add("");
        lines.add("  private ArrayList<Value> fetch_all(String prefix) {");
        lines.add("    ArrayList<Value> values = new ArrayList<>();");
        lines.add("    values.addAll(storage.scan(prefix).values());");
        lines.add("    return values;");
        lines.add("  }");

        lines.add("");
        lines.add("  private ArrayList<Value> fetch(HashSet<String> keys) {"); // Should we accept scope?
        lines.add("    ArrayList<Value> values = new ArrayList<>();");
        lines.add("    if (keys == null) {");
        lines.add("      return values;");
        lines.add("    }");
        lines.add("    for (String key : keys) {");
        lines.add("      Value value = storage.get(key);");
        lines.add("        if (value != null) {");
        lines.add("          values.add(value);");
        lines.add("        }");
        lines.add("      }");
        lines.add("    return values;");
        lines.add("  }");
    }
}
