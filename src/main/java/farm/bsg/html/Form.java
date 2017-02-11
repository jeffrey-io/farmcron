package farm.bsg.html;

public class Form extends HtmlPump {
    private final String href;

    private final String method;
    private HtmlPump     inner;

    public Form(String method, String href) {
        this.method = method;
        this.href = href;
        this.inner = null;
    }

    public Form inner(HtmlPump inner) {
        this.inner = inner;
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<form method=\"").append(method).append("\" action=\"").append(href).append("\"");
        // other stuff here, eventually
        html.append(">");
        if (inner != null) {
            inner.pump(html);

        }
        html.append("</form>");
    }
}
