package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;

public class Writes {
    public static void write(ArrayList<String> lines, String name, ObjectSchema object) {
        String suffixArgs = ", false";
        if (object.isEphemeral()) {
            suffixArgs = ", true";
        }

        lines.add("");
        lines.add("  public PutResult put(" + name + " " + name.toLowerCase() + ") {");
        lines.add("    return storage.put(" + name.toLowerCase() + ".getStorageKey(), new Value(" + name.toLowerCase() + ".toJson())" + suffixArgs + ");");
        lines.add("  }");

        lines.add("");
        lines.add("  public PutResult del(" + name + " " + name.toLowerCase() + ") {");
        lines.add("    return storage.put(" + name.toLowerCase() + ".getStorageKey(), null" + suffixArgs + ");");
        lines.add("  }");
    }

}
