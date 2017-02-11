package farm.bsg.html.shit;

import java.util.HashSet;

import farm.bsg.data.RawObject;
import farm.bsg.data.Type;

public class ObjectModelForm {

    public static String htmlOf(RawObject o) {
        StringBuilder sb = new StringBuilder();

        for (Type t : o.getTypes()) {
            String value = o.get(t.name());
            if (value != null) {
                value = "value=\"" + value + "\" ";
            }
            sb.append(":" + t.name());
            sb.append("<input type=\"text\" name=\"" + t.name() + "\" " + value + "/><br />\n");
        }
        sb.append("<input type=\"checkbox\" id=\"_delete_\" name=\"_delete_\" value=\"_delete_\"/>");
        return sb.toString();
    }

    public static String transferObjectInHiddenFieldsExceptSkipped(RawObject o, String... toSkip) {
        HashSet<String> set = new HashSet<>();
        for (String skip : toSkip) {
            set.add(skip);
        }
        StringBuilder sb = new StringBuilder();
        for (Type t : o.getTypes()) {
            if (set.contains(t.name())) {
                continue;
            }
            String value = o.get(t.name());
            if (value != null) {
                sb.append("<input type=\"hidden\" name=\"" + t.name() + "\" value=\"" + value + "\" />");
            }
        }
        return sb.toString();
    }
}
