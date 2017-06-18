package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;

public class Writes {
    public static void write(ArrayList<String> lines, String name, ObjectSchema object) {
        lines.add("");
        lines.add("  public PutResult put(" + name + " " + name.toLowerCase() + ") {");
        lines.add("    return storage.put(" + name.toLowerCase() + ".getStorageKey(), new Value(" + name.toLowerCase() + ".toJson()));");
        lines.add("  }");
        
        lines.add("");
        lines.add("  public PutResult del(" + name + " " + name.toLowerCase() + ") {");
        lines.add("    return storage.put(" + name.toLowerCase() + ".getStorageKey(), null);");
        lines.add("  }");        
    }

}
