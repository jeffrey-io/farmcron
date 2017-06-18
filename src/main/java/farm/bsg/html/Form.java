package farm.bsg.html;

public class Form extends HtmlPump {
    private final String href;

    private final String method;
    private HtmlPump     inner;
    private boolean      multipartFormData;

    public Form(String method, String href) {
        this.method = method;
        this.href = href;
        this.inner = null;
        this.multipartFormData = false;
    }

    public Form inner(HtmlPump inner) {
        this.inner = inner;
        return this;
    }
    
    public Form multipart() {
        this.multipartFormData = true;
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<form method=\"").append(method).append("\" action=\"").append(href).append("\"");
        if (multipartFormData) {
            html.append(" enctype=\"multipart/form-data\"");
        }
        html.append(">");
        if (inner != null) {
            inner.pump(html);

        }
        html.append("</form>");
    }
}
