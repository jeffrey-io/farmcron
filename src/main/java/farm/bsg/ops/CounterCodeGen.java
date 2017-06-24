package farm.bsg.ops;

import java.util.ArrayList;
import java.util.HashSet;

public class CounterCodeGen {
    
    private static enum LazyType {
        Event, EventAlarm, Histogram
    }

    private class LazyCounter {
        public final String  section;
        public final LazyType type;
        public final String  name;
        public final String  description;

        public LazyCounter(String section, LazyType type, String name, String description) {
            this.section = section;
            this.type = type;
            this.name = name.trim().toLowerCase().replaceAll(" ", "_");
            this.description = description;
        }
    }

    private final ArrayList<LazyCounter> lazy;
    private String                       nextSection;
    private final HashSet<String>        unique;

    public CounterCodeGen() {
        this.lazy = new ArrayList<>();
        this.unique = new HashSet<>();
    }

    public void section(String nextSection) {
        this.nextSection = nextSection;
    }

    public void counter(String name, String description) {
        if (unique.contains(name)) {
            throw new RuntimeException(name + " is not unique");
        }
        unique.add(name);
        lazy.add(new LazyCounter(nextSection, LazyType.Event, name, description));
        nextSection = null;
    }

    public void histogram(String name, String description) {
        if (unique.contains(name)) {
            throw new RuntimeException(name + " is not unique");
        }
        unique.add(name);
        lazy.add(new LazyCounter(nextSection, LazyType.Histogram, name, description));
        nextSection = null;
    }

    public void alarm(String name, String description) {
        if (unique.contains(name)) {
            throw new RuntimeException(name + " is not unique");
        }
        unique.add(name);
        lazy.add(new LazyCounter(nextSection, LazyType.EventAlarm, name, description));
        nextSection = null;
    }

    public String java(String javaPackage, String className) {
        StringBuilder sb = new StringBuilder();

        sb.append("package farm.bsg;\n");
        sb.append("\n");
        sb.append("import farm.bsg.ops.Counter;\n");
        sb.append("import farm.bsg.ops.CounterSource;\n");
        sb.append("\n");
        sb.append("public class " + className + " {\n");
        for (LazyCounter counter : lazy) {
            if (counter.section != null) {
                sb.append("\n");
                sb.append("  // Section{" + counter.section.toUpperCase() + "}\n");
            }
            sb.append("  public final Counter " + counter.name + ";\n");
        }
        sb.append("\n");
        sb.append("  public final CounterSource source;\n");
        sb.append("\n");
        sb.append("  public static final " + className + " I = BUILD();\n");
        sb.append("\n");
        sb.append("  private static " + className + " BUILD() {\n");
        sb.append("    CounterSource source = new CounterSource();\n");
        sb.append("    " + className + " counters = new " + className + "(source);\n");
        sb.append("    return counters;\n");
        sb.append("  }\n");
        sb.append("\n");
        sb.append("  public " + className + "(CounterSource src) {\n");
        sb.append("    this.source = src;\n");
        for (LazyCounter counter : lazy) {
            if (counter.section != null) {
                sb.append("\n");
                sb.append("    src.setSection(\"" + counter.section + "\");\n");
            }
            switch (counter.type) {
                case Event:
                    sb.append("    this." + counter.name + " = src.counter(\"" + counter.name + "\", \"" + counter.description + "\");\n");
                    break;
                case EventAlarm:
                    sb.append("    this." + counter.name + " = src.alarm(\"" + counter.name + "\", \"" + counter.description + "\");\n");
                    break;
                case Histogram:
                    sb.append("    this." + counter.name + " = src.histogram(\"" + counter.name + "\", \"" + counter.description + "\");\n");
                    break;
            }
        }

        sb.append("  }\n");
        sb.append("}\n");
        return sb.toString();

    }

}
