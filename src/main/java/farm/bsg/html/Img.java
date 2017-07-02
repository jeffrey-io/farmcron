package farm.bsg.html;

public class Img extends HtmlPump {

    private String contentType = null;
    private String content = null;
    private Integer width = null;
    private Integer height = null;
    
    public Img() {
    }
    
    public Img content(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
        return this;
    }
    
    public Img size(Integer w, Integer h) {
        this.width = w;
        this.height = h;
        return this;
    }
    
    public Img width(Integer w) {
        this.width = w;
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<img src=\"");
        if (contentType != null && content != null) {
            html.append("data:").append(contentType).append(";base64,").append(content);
        }
        html.append("\" ");
        if (width != null) {
            html.append("width=\"").append(width).append("\"");
        }
        if (height != null) {
            html.append("height=\"").append(height).append("\"");
        }

        html.append("/>");
    }
}
