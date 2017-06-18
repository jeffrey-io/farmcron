package farm.bsg.data.codegen;

import java.util.ArrayList;

import farm.bsg.data.ObjectSchema;
import farm.bsg.data.Type;

public class StackQueryEngine {

    public static void write(ArrayList<String> lines, String name, ObjectSchema object) {
        String keyIndexPrefix = name.toLowerCase() + "_";
        lines.add("");
        lines.add("  public " + name + "SetQuery select_" + name.toLowerCase() + "() {");
        lines.add("    return new " + name + "SetQuery();");
        lines.add("  }");

        lines.add("");
        lines.add("  public class " + name + "ListHolder {");
        lines.add("    private final ArrayList<" + name + "> list;");
        lines.add("");
        lines.add("    private " + name + "ListHolder(HashSet<String> keys, String scope) {");
        String func = name.toLowerCase() + "s_of(";
        lines.add("      if (keys == null) {");
        lines.add("        this.list = " + func + "fetch_all(\"" + object.getPrefix() + "\" + scope));");
        lines.add("      } else {");
        lines.add("        this.list = " + func + "fetch(keys));");
        lines.add("      }");
        lines.add("    }");

        lines.add("");
        lines.add("    private " + name + "ListHolder(ArrayList<" + name + "> list) {");
        lines.add("      this.list = list;");
        lines.add("    }");

        lines.add("");
        lines.add("    public " + name + "ListHolder inline_filter(Predicate<" + name + "> filter) {");
        lines.add("      Iterator<" + name + "> it = list.iterator();");
        lines.add("      while (it.hasNext()) {");
        lines.add("        if (filter.test(it.next())) {");
        lines.add("          it.remove();");
        lines.add("        }");
        lines.add("      }");
        lines.add("      return this;");
        lines.add("    }");

        
        lines.add("");
        lines.add("    public " + name + "ListHolder limit(int count) {");
        lines.add("      Iterator<" + name + "> it = list.iterator();");
        lines.add("      int at = 0;");
        lines.add("      while (it.hasNext()) {");
        lines.add("        it.next();");
        lines.add("        if (at >= count) {");
        lines.add("          it.remove();");
        lines.add("        }");
        lines.add("        at++;");
        lines.add("      }");
        lines.add("      return this;");
        lines.add("    }");

        lines.add("");
        lines.add("    public " + name + "ListHolder inline_apply(Consumer<" + name + "> consumer) {");
        lines.add("      Iterator<" + name + "> it = list.iterator();");
        lines.add("      while (it.hasNext()) {");
        lines.add("        consumer.accept(it.next());");
        lines.add("      }");
        lines.add("      return this;");
        lines.add("    }");

        lines.add("");
        lines.add("    public " + name + "ListHolder fork() {");
        lines.add("      return new " + name + "ListHolder(this.list);");
        lines.add("    }");

        lines.add("");
        lines.add("    public " + name + "ListHolder inline_order_lexographically_asc_by(String... keys) {");
        lines.add("      Collections.sort(this.list, new LexographicalOrder<" + name + ">(keys, true, true));");
        lines.add("      return this;");
        lines.add("    }");
        lines.add("");
        lines.add("    public " + name + "ListHolder inline_order_lexographically_desc_by(String... keys) {");
        lines.add("      Collections.sort(this.list, new LexographicalOrder<" + name + ">(keys, false, true));");
        lines.add("      return this;");
        lines.add("    }");
        lines.add("");
        lines.add("    public " + name + "ListHolder inline_order_lexographically_by(boolean asc, boolean caseSensitive, String... keys) {");
        lines.add("      Collections.sort(this.list, new LexographicalOrder<" + name + ">(keys, asc, caseSensitive));");
        lines.add("      return this;");
        lines.add("    }");
        lines.add("");
        lines.add("    public " + name + "ListHolder inline_order_by(Comparator<"+name+"> comparator) {");
        lines.add("      Collections.sort(this.list, comparator);");
        lines.add("      return this;");
        lines.add("    }");
        lines.add("");
        lines.add("    public int count() {");
        lines.add("      return this.list.size();");
        lines.add("    }");

        lines.add("    public " + name + " first() {");
        lines.add("      if (this.list.size() == 0) {");
        lines.add("        return null;");
        lines.add("      }");
        lines.add("      return this.list.get(0);");
        lines.add("    }");

        lines.add("    public ArrayList<" + name + "> done() {");
        lines.add("      return this.list;");
        lines.add("    }");

        lines.add("  }");

        lines.add("");
        lines.add("  public class " + name + "SetQuery {");
        lines.add("    private String scope;");
        lines.add("    private HashSet<String> keys;");
        lines.add("");
        lines.add("    private " + name + "SetQuery() {");
        lines.add("      this.scope = \"\";");
        lines.add("      this.keys = null;");
        lines.add("    }");

        lines.add("");
        lines.add("    public " + name + "ListHolder to_list() {");
        lines.add("      return new " + name + "ListHolder(this.keys, this.scope);");
        lines.add("    }");

        lines.add("");
        lines.add("    public ArrayList<" + name + "> done() {");
        lines.add("      return new " + name + "ListHolder(this.keys, this.scope).done();");
        lines.add("    }");

        lines.add("");
        lines.add("    public int count() {");
        lines.add("      if (this.keys == null) {");
        lines.add("        return to_list().count();");
        lines.add("      } else {");
        lines.add("        return this.keys.size();");
        lines.add("      }");
        lines.add("    }");

        lines.add("");
        lines.add("    public " + name + "SetQuery scope(String scope) {");
        lines.add("      this.scope += scope + \"/\";");
        lines.add("      return this;");
        lines.add("    }");

        for (Type type : object.getTypes()) {
            if (type.isIndexed()) {
                // fetch keys from a list of values
                lines.add("");
                lines.add("    private HashSet<String> lookup_" + type.name() + "(String... values) {");
                lines.add("      HashSet<String> keys = new HashSet<>();");
                lines.add("      for(String value : values) {");
                lines.add("        keys.addAll(" + keyIndexPrefix + type.name() + ".getKeys(value));");
                lines.add("      }");
                lines.add("      return keys;");
                lines.add("    }");
                lines.add("");
                lines.add("    public " + name + "SetQuery where_" + type.name() + "_eq(String... values) {");
                lines.add("      if (this.keys == null) {");
                lines.add("        this.keys = lookup_" + type.name() + "(values);");
                lines.add("      } else {");
                lines.add("        this.keys = BinaryOperators.intersect(this.keys, lookup_" + type.name() + "(values));");
                lines.add("      }");
                lines.add("      return this;");
                lines.add("    }");
            }
        }
        lines.add("  }");
    }
}
