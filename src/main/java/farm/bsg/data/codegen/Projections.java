package farm.bsg.data.codegen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeSet;

import farm.bsg.data.ObjectSchema;
import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class Projections {
    public static void write(ArrayList<String> lines, String name, ObjectSchema object) {

        TreeSet<String> projections = new TreeSet<>();
        for (Type type : object.getTypes()) {
            for (String mutation : type.getProjections()) {
                projections.add(mutation);
            }
        }

        for (String projection : projections) {
            ArrayList<Type> types = new ArrayList<>();
            for (Type type : object.getTypes()) {
                if (type.getProjections().contains(projection)) {
                    types.add(type);
                }
            }

            lines.add("");
            lines.add("  public class " + name + "Projection_" + projection + " {");
            lines.add("    private final HashMap<String, String> data;");
            lines.add("    ");
            lines.add("    public " + name + "Projection_" + projection + "(ProjectionProvider pp) {");
            lines.add("      this.data = new HashMap<String, String>();");
            for (Type type : types) {
                if (!hasProjection(type)) {
                    throw new RuntimeException("Type '" + type.getClass().getName() + "' has no static project method");
                }
                lines.add("      this.data.put(\"" + type.name() + "\", " + type.getClass().getName() + ".project(pp, \"" + type.name() + "\"));");
            }
            lines.add("    }");
            lines.add("");

            lines.add("    public PutResult apply(" + name + " " + name.toLowerCase() + ") {");
            lines.add("      return " + name.toLowerCase() + ".validateAndApplyProjection(this.data);");
            lines.add("    }");

            lines.add("  }");

            lines.add("");
            lines.add("  public " + name + "Projection_" + projection + " projection_" + name.toLowerCase() + "_" + projection + "_of(ProjectionProvider pp) {");
            lines.add("    return new " + name + "Projection_" + projection + "(pp);");
            lines.add("  }");
            lines.add("");
        }

    }

    private static boolean hasProjection(Type type) {
        try {
            Method method = type.getClass().getMethod("project", ProjectionProvider.class, String.class);
            return method != null;
        } catch (Exception err) {
            return false;
        }
    }
}
