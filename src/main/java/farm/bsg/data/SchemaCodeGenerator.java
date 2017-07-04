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
    private final String                        javaPackage;
    private final String                        className;
    private final Imports                       imports = new Imports();

    public SchemaCodeGenerator(final String javaPackage, final String className) {
        this.schemas = new TreeMap<>();
        this.javaPackage = javaPackage;
        this.className = className;
    }

    public <T extends RawObject> void addSample(final T object) {
        this.imports.add(object.getClass().getName());
        this.schemas.put(object.getClass().getSimpleName(), object.getSchema());
    }

    private String finishAndBuild(final ArrayList<String> lines) {
        lines.add("}");
        final StringBuilder sb = new StringBuilder();
        for (final String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public String java() {
        final ArrayList<String> lines = new ArrayList<>();
        writeClassIntro(lines);
        writeClassFields(lines);
        writeConstructor(lines);
        Helpers.write(lines);
        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            section(lines, "Basic Operations (look up, type transfer, keys, immutable copies)", entry.getValue());
            Lookups.write(lines, entry.getKey(), entry.getValue());
            TypeTransfer.write(lines, entry.getKey(), entry.getValue());
            KeyFactory.write(lines, entry.getKey(), entry.getValue());
        }

        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            section(lines, "Indexing", entry.getValue());
            IndexingEngine.write(lines, entry.getKey(), entry.getValue());
        }

        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            section(lines, "Query Engine", entry.getValue());
            StackQueryEngine.write(lines, entry.getKey(), entry.getValue());
        }

        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            section(lines, "Projects", entry.getValue());
            Projections.write(lines, entry.getKey(), entry.getValue());
        }

        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            section(lines, "Writing Back to DB", entry.getValue());
            Writes.write(lines, entry.getKey(), entry.getValue());
        }

        return finishAndBuild(lines);
    }

    private void section(final ArrayList<String> lines, final String title, final ObjectSchema sample) {
        lines.add("");
        lines.add("  /**************************************************");
        lines.add("  " + title + " (" + sample.getPrefix() + ")");
        lines.add("  **************************************************/");
    }

    private void writeClassFields(final ArrayList<String> lines) {
        lines.add("  public final MultiPrefixLogger indexing;");
        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            IndexingEngine.writeFields(lines, entry.getKey(), entry.getValue());
        }
        lines.add("  public final StorageEngine storage;");
        lines.add("  public final ExecutorService executor;");
        lines.add("  public final ScheduledExecutorService scheduler;");
        lines.add("  public final UriBlobCache publicBlobCache;");
    }

    private void writeClassIntro(final ArrayList<String> lines) {
        lines.add("package " + this.javaPackage + ";");
        this.imports.write(lines);
        lines.add("/****************************************************************");
        lines.add("WARNING: Generated");
        lines.add("This class is a generated database query engine that provides an");
        lines.add("easy to take a simple Persistence mechanism into a real DB");
        lines.add("****************************************************************/");
        lines.add("public class " + this.className + " {");
    }

    private void writeConstructor(final ArrayList<String> lines) {
        lines.add("");
        lines.add("  public " + this.className + "(PersistenceLogger persistence) throws Exception {");
        lines.add("    InMemoryStorage memory = new InMemoryStorage();");
        lines.add("    this.executor = Executors.newFixedThreadPool(2);");
        lines.add("    this.scheduler = Executors.newSingleThreadScheduledExecutor();");
        lines.add("    this.publicBlobCache = new UriBlobCache();");
        lines.add("    this.indexing = new MultiPrefixLogger();");
        for (final Entry<String, ObjectSchema> entry : this.schemas.entrySet()) {
            IndexingEngine.writeConstructor(lines, entry.getKey(), entry.getValue());
        }
        lines.add("    this.storage = new StorageEngine(memory, indexing, persistence);");
        lines.add("  }");
    }

}
