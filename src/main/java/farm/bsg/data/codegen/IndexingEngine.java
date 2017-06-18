package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;
import farm.bsg.data.Type;

public class IndexingEngine {

    public static void writeConstructor(ArrayList<String> lines, String name, ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        String keyIndexPrefix = name.toLowerCase() + "_";
        for (Type type : object.getTypes()) {
            if (type.isIndexed()) {
                lines.add("    this." + keyIndexPrefix + type.name() + " = indexing.add(\"" + object.getPrefix() + "\", new KeyIndex(\"" + type.name() + "\", " + type.isIndexedUniquely() + "));");
            }
        }
        for (String javaType : object.getDirtyBitIndicesJavaTypes()) {
            lines.add("    this.indexing.add(\"" + object.getPrefix() + "\", new " + javaType + "(this));");
        }
    }

    public static void writeFields(ArrayList<String> lines, String name, ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        String keyIndexPrefix = name.toLowerCase() + "_";
        boolean first = true;
        for (Type type : object.getTypes()) {
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

    public static void write(ArrayList<String> lines, String name, ObjectSchema object) {
        if (object.isSingleton()) {
            return;
        }
        String keyIndexPrefix = name.toLowerCase() + "_";
        for (Type type : object.getTypes()) {
            if (type.isIndexed()) {
                lines.add("");
                lines.add("  public HashSet<String> get_" + keyIndexPrefix + type.name() + "_index_keys() {");
                lines.add("    return " + keyIndexPrefix + type.name() + ".getIndexKeys();");
                lines.add("  }");
            }
        }
    }
}
