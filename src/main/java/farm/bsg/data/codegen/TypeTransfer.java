package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.RawObject;

public class TypeTransfer {

    public static void write(ArrayList<String> lines, String name, RawObject object) {
        lines.add("");
        lines.add("  private " + name + " " + name.toLowerCase() + "_of(Value v) {");
        lines.add("    " + name + " item = new " + name + "();");
        lines.add("    if (v != null) {");
        lines.add("      item.injectValue(v);");
        lines.add("    }");
        lines.add("    return item;");
        lines.add("  }");
        lines.add("");
        lines.add("  private ArrayList<" + name + "> " + name.toLowerCase() + "s_of(ArrayList<Value> values) {");
        lines.add("    ArrayList<" + name + "> list = new ArrayList<>(values.size());");
        lines.add("    for (Value v : values) {");
        lines.add("      list.add(" + name.toLowerCase() + "_of(v));");
        lines.add("    }");
        lines.add("    return list;");
        lines.add("  }");
    }

}
