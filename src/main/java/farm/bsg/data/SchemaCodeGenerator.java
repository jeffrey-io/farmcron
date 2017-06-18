package farm.bsg.data;

import java.util.ArrayList;
import java.util.Map.Entry;

import java.util.TreeMap;
import farm.bsg.data.codegen.Helpers;
import farm.bsg.data.codegen.Imports;
import farm.bsg.data.codegen.IndexingEngine;
import farm.bsg.data.codegen.KeyFactory;
import farm.bsg.data.codegen.Lookups;
import farm.bsg.data.codegen.Projections;
import farm.bsg.data.codegen.StackQueryEngine;
import farm.bsg.data.codegen.TypeTransfer;
import farm.bsg.data.codegen.Writes;

public class SchemaCodeGenerator {

    private final TreeMap<String, ObjectSchema> schemas;
    private String                           javaPackage;
    private String                           className;
    private Imports imports = new Imports();

    public SchemaCodeGenerator(String javaPackage, String className) {
        this.schemas = new TreeMap<>();
        this.javaPackage = javaPackage;
        this.className = className;
    }

    public <T extends RawObject> void addSample(T object) {
        imports.add(object.getClass().getName());
        schemas.put(object.getClass().getSimpleName(), object.getSchema());
    }

    private void writeClassIntro(ArrayList<String> lines) {
        lines.add("package " + javaPackage + ";");
        imports.write(lines);
        lines.add("/****************************************************************");
        lines.add("WARNING: Generated");
        lines.add("This class is a generated database query engine that provides an");
        lines.add("easy to take a simple Persistence mechanism into a real DB");
        lines.add("****************************************************************/");
        lines.add("public class " + className + " {");
    }

    private void writeClassFields(ArrayList<String> lines) {
        lines.add("  public final MultiPrefixLogger indexing;");
        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            IndexingEngine.writeFields(lines, entry.getKey(), entry.getValue());
        }
        lines.add("  public final StorageEngine storage;");
    }

    private void writeConstructor(ArrayList<String> lines) {
        lines.add("");
        lines.add("  public " + className + "(PersistenceLogger persistence) throws Exception {");
        lines.add("    InMemoryStorage memory = new InMemoryStorage();");
        lines.add("    this.indexing = new MultiPrefixLogger();");
        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            IndexingEngine.writeConstructor(lines, entry.getKey(), entry.getValue());
        }
        lines.add("    this.storage = new StorageEngine(memory, indexing, persistence);");
        lines.add("  }");
    }

    private String finishAndBuild(ArrayList<String> lines) {
        lines.add("}");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private void section(ArrayList<String> lines, String title, ObjectSchema sample) {
        lines.add("");
        lines.add("  /**************************************************");
        lines.add("  " + title + " (" + sample.getPrefix() + ")");
        lines.add("  **************************************************/");
    }

    public String java() {
        ArrayList<String> lines = new ArrayList<>();
        writeClassIntro(lines);
        writeClassFields(lines);
        writeConstructor(lines);
        Helpers.write(lines);
        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            section(lines, "Basic Operations (look up, type transfer, keys, immutable copies)", entry.getValue());
            Lookups.write(lines, entry.getKey(), entry.getValue());
            TypeTransfer.write(lines, entry.getKey(), entry.getValue());
            KeyFactory.write(lines, entry.getKey(), entry.getValue());
        }

        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            section(lines, "Indexing", entry.getValue());
            IndexingEngine.write(lines, entry.getKey(), entry.getValue());
        }

        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            section(lines, "Query Engine", entry.getValue());
            StackQueryEngine.write(lines, entry.getKey(), entry.getValue());
        }

        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            section(lines, "Projects", entry.getValue());
            Projections.write(lines, entry.getKey(), entry.getValue());
        }

        
        for (Entry<String, ObjectSchema> entry : schemas.entrySet()) {
            section(lines, "Writing Back to DB", entry.getValue());
            Writes.write(lines, entry.getKey(), entry.getValue());
        }

        return finishAndBuild(lines);
    }

}
