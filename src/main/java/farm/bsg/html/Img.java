package farm.bsg.html;

public class Img extends HtmlPump {

    private String  contentType = null;
    private String  content     = null;
    private Integer width       = null;
    private Integer height      = null;

    public Img() {
    }

    public Img content(final String contentType, final String content) {
        this.contentType = contentType;
        this.content = content;
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<img src=\"");
        if (this.contentType != null && this.content != null) {
            html.append("data:").append(this.contentType).append(";base64,").append(this.content);
        }
        html.append("\" ");
        if (this.width != null) {
            html.append("width=\"").append(this.width).append("\"");
        }
        if (this.height != null) {
            html.append("height=\"").append(this.height).append("\"");
        }

        html.append("/>");
    }

    public Img size(final Integer w, final Integer h) {
        this.width = w;
        this.height = h;
        return this;
    }

    public Img width(final Integer w) {
        this.width = w;
        return this;
    }
}
