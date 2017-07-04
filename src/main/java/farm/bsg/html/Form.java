package farm.bsg.html;

public class Form extends HtmlPump {
    private final String href;

    private String       clazz;
    private final String method;
    private HtmlPump     inner;
    private boolean      multipartFormData;
    private String       id;

    public Form(final String method, final String href) {
        this.method = method;
        this.href = href;
        this.inner = null;
        this.multipartFormData = false;
        this.clazz = null;
    }

    public Form clazz(final String clazz) {
        this.clazz = clazz;
        return this;
    }

    public Form inner(final HtmlPump inner) {
        this.inner = inner;
        return this;
    }

    public Form multipart() {
        this.multipartFormData = true;
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<form method=\"").append(this.method).append("\" action=\"").append(this.href).append("\"");
        if (this.multipartFormData) {
            html.append(" enctype=\"multipart/form-data\"");
        }
        if (this.clazz != null) {
            html.append(" class=\"").append(this.clazz).append("\"");
        }
        if (this.id != null) {
            html.append(" id=\"").append(this.id).append("\"");
        }
        html.append(">");
        if (this.inner != null) {
            this.inner.pump(html);

        }
        html.append("</form>");
    }

    public Form withId(final String id) {
        this.id = id;
        return this;
    }
}
