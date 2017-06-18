package farm.bsg.data.codegen;

import java.util.ArrayList;
import java.util.TreeSet;

public class Imports {

    private final TreeSet<String> imports = new TreeSet<>();

    public void add(String i) {
        imports.add(i);
    }

    public void write(ArrayList<String> lines) {
        imports.add("java.util.ArrayList");
        imports.add("farm.bsg.data.MultiPrefixLogger");
        imports.add("farm.bsg.data.KeyIndex");
        imports.add("java.util.HashSet");
        imports.add("farm.bsg.data.*");
        imports.add("farm.bsg.data.contracts.*");
        imports.add("java.util.Collections");
        imports.add("java.util.function.Predicate");
        imports.add("java.util.function.Consumer");
        imports.add("java.util.Iterator");
        imports.add("java.util.HashMap");
        imports.add("java.util.Comparator");
        imports.add("java.util.concurrent.ExecutorService");
        imports.add("java.util.concurrent.Executors");
        imports.add("java.util.concurrent.ScheduledExecutorService");

        String prefix = "";
        for (String i : imports) {
            String currentPrefix = i.substring(0, i.lastIndexOf('.'));
            if (!currentPrefix.equals(prefix)) {
                lines.add("");
            }
            lines.add("import " + i + ";");
            prefix = currentPrefix;
        }
        lines.add("");
    }
}
