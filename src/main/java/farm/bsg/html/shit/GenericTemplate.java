package farm.bsg.html.shit;

import java.util.regex.Pattern;

public class GenericTemplate {
    private static final String TITLE  = Pattern.quote("$TITLE$");
    private static final String BODY   = Pattern.quote("$BODY$");
    private static final String NAVBAR = Pattern.quote("$NAVBAR$");

    private final String        template;

    public GenericTemplate(String template) {
        this.template = template;
    }

    public String html(String title, String navbar, String body) {
        String result = template;
        result = result.replaceAll(TITLE, title);
        result = result.replaceAll(NAVBAR, navbar);
        result = result.replaceAll(BODY, body);
        return result;
    }
}
