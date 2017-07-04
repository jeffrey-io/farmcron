package farm.bsg.data.codegen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeSet;

import farm.bsg.data.ObjectSchema;
import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class Projections {
    private static boolean hasProjection(final Type type) {
        try {
            final Method method = type.getClass().getMethod("project", ProjectionProvider.class, String.class);
            return method != null;
        } catch (final Exception err) {
            return false;
        }
    }

    public static void write(final ArrayList<String> lines, final String name, final ObjectSchema object) {

        final TreeSet<String> projections = new TreeSet<>();
        for (final Type type : object.getTypes()) {
            for (final String mutation : type.getProjections()) {
                projections.add(mutation);
            }
        }

        for (final String projection : projections) {
            final ArrayList<Type> types = new ArrayList<>();
            for (final Type type : object.getTypes()) {
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
            for (final Type type : types) {
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
}
