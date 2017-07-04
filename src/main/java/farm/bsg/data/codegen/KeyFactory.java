package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;
import farm.bsg.data.Type;

public class KeyFactory {

    public static void write(final ArrayList<String> lines, final String name, final ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        final StringBuilder funcDef = new StringBuilder();
        funcDef.append("  public String make_key_" + name.toLowerCase() + "(");
        for (final Type type : object.getTypes()) {
            if (type.isScoped()) {
                funcDef.append("String ").append(type.name()).append(", ");
            }
        }
        funcDef.append("String id) {");
        lines.add("");
        lines.add(funcDef.toString());
        lines.add("    StringBuilder key = new StringBuilder();");
        lines.add("    key.append(\"" + object.getPrefix() + "\");");
        for (final Type type : object.getTypes()) {
            if (type.isScoped()) {
                lines.add("    key.append(" + type.name() + ");");
                lines.add("    key.append(\"/\");");
            }
        }
        lines.add("    key.append(id);");
        lines.add("    return key.toString();");
        lines.add("}");
    }

}
