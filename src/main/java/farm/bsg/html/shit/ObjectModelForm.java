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

}
