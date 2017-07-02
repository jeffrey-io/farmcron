package farm.bsg.html.shit;

import java.util.regex.Pattern;

public class GenericTemplate {
    private static final String TITLE  = Pattern.quote("$TITLE$");
    private static final String NAVBAR = Pattern.quote("$NAVBAR$");

    private final String        template;

    public GenericTemplate(String template) {
        this.template = template;
    }

    public String html(String title, String navbar, String body) {
        String result = template;
        result = result.replaceAll(TITLE, title);
        result = result.replaceAll(NAVBAR, navbar);
        return replaceOnce(result, "$BODY$", body);
    }
    
    private String replaceOnce(String item, String pattern, String replace) {
        int index = item.indexOf(pattern);
        return item.substring(0, index) + replace + item.substring(index + pattern.length());
    }
}
