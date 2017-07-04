package farm.bsg.data.codegen;

import java.util.ArrayList;
import java.util.TreeSet;

public class Imports {

    private final TreeSet<String> imports = new TreeSet<>();

    public void add(final String i) {
        this.imports.add(i);
    }

    public void write(final ArrayList<String> lines) {
        this.imports.add("java.util.ArrayList");
        this.imports.add("farm.bsg.data.MultiPrefixLogger");
        this.imports.add("farm.bsg.data.KeyIndex");
        this.imports.add("java.util.HashSet");
        this.imports.add("farm.bsg.data.*");
        this.imports.add("farm.bsg.data.contracts.*");
        this.imports.add("java.util.Collections");
        this.imports.add("java.util.function.*");
        this.imports.add("java.util.Iterator");
        this.imports.add("java.util.HashMap");
        this.imports.add("java.util.Comparator");
        this.imports.add("java.util.concurrent.ExecutorService");
        this.imports.add("java.util.concurrent.Executors");
        this.imports.add("java.util.concurrent.ScheduledExecutorService");

        String prefix = "";
        for (final String i : this.imports) {
            final String currentPrefix = i.substring(0, i.lastIndexOf('.'));
            if (!currentPrefix.equals(prefix)) {
                lines.add("");
            }
            lines.add("import " + i + ";");
            prefix = currentPrefix;
        }
        lines.add("");
    }
}
