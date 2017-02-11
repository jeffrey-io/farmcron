package farm.bsg.html;

public class Tag extends HtmlPump {

    private String   suffix;
    private boolean  pill;
    private HtmlPump content;

    public Tag() {
        this.suffix = "default";
        this.pill = false;
    }

    public Tag primary() {
        this.suffix = "primary";
        return this;
    }

    public Tag success() {
        this.suffix = "success";
        return this;
    }

    public Tag info() {
        this.suffix = "info";
        return this;
    }

    public Tag warning() {
        this.suffix = "warning";
        return this;
    }

    public Tag danger() {
        this.suffix = "danger";
        return this;
    }

    public Tag pill() {
        this.pill = true;
        return this;
    }

    public Tag content(HtmlPump pump) {
        this.content = pump;
        return this;
    }

    public Tag content(String html) {
        this.content = new Text(html);
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<span class=\"tag ");
        if (pill) {
            html.append("tag-pill ");
        }
        html.append("tag-").append(suffix).append("\">");
        content.pump(html);
        html.append("</span>");
    }

}
