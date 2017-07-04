package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;
import farm.bsg.data.Type;

public class IndexingEngine {

    public static void write(final ArrayList<String> lines, final String name, final ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        final String keyIndexPrefix = name.toLowerCase() + "_";
        for (final Type type : object.getTypes()) {
            if (type.isIndexed()) {
                lines.add("");
                lines.add("  public HashSet<String> get_" + keyIndexPrefix + type.name() + "_index_keys() {");
                lines.add("    return " + keyIndexPrefix + type.name() + ".getIndexKeys();");
                lines.add("  }");
            }
        }
    }

    public static void writeConstructor(final ArrayList<String> lines, final String name, final ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        final String keyIndexPrefix = name.toLowerCase() + "_";
        for (final Type type : object.getTypes()) {
            if (type.isIndexed()) {
                lines.add("    this." + keyIndexPrefix + type.name() + " = indexing.add(\"" + object.getPrefix() + "\", new KeyIndex(\"" + type.name() + "\", " + type.isIndexedUniquely() + "));");
            }
        }
        for (final String javaType : object.getDirtyBitIndicesJavaTypes()) {
            lines.add("    this.indexing.add(\"" + object.getPrefix() + "\", new " + javaType + "(this));");
        }
    }

    public static void writeFields(final ArrayList<String> lines, final String name, final ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        final String keyIndexPrefix = name.toLowerCase() + "_";
        boolean first = true;
        for (final Type type : object.getTypes()) {
            if (type.isIndexed()) {
                if (first) {
                    lines.add("");
                    lines.add("  // INDEX[" + name + "]");
                    first = false;
                }
                lines.add("  public final KeyIndex " + keyIndexPrefix + type.name() + ";  // BY[" + type.name() + "]");
            }
        }
    }
}
