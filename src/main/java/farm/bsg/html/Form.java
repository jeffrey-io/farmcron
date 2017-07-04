package farm.bsg.html;

public class Form extends HtmlPump {
    private final String href;

    private String clazz;
    private final String method;
    private HtmlPump     inner;
    private boolean      multipartFormData;
    private String id;

    public Form(String method, String href) {
        this.method = method;
        this.href = href;
        this.inner = null;
        this.multipartFormData = false;
        this.clazz = null;
    }

    public Form inner(HtmlPump inner) {
        this.inner = inner;
        return this;
    }

    public Form clazz(String clazz) {
        this.clazz = clazz;
        return this;
    }
    
    public Form multipart() {
        this.multipartFormData = true;
        return this;
    }
    
    public Form withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<form method=\"").append(method).append("\" action=\"").append(href).append("\"");
        if (multipartFormData) {
            html.append(" enctype=\"multipart/form-data\"");
        }
        if (clazz != null) {
            html.append(" class=\"").append(clazz).append("\"");
        }
        if (id != null) {
            html.append(" id=\"").append(id).append("\"");
        }
        html.append(">");
        if (inner != null) {
            inner.pump(html);

        }
        html.append("</form>");
    }
}
