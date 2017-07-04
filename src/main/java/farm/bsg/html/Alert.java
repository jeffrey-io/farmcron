package farm.bsg.html;

public class Alert extends HtmlPump {

    private final HtmlPump pump;
    private String         mode;
    private int            headingSize = 4;
    private String         heading;

    public Alert(final HtmlPump pump) {
        this.pump = pump;
        this.mode = "info";
        this.heading = null;
    }

    public Alert danger() {
        this.mode = "danger";
        return this;
    }

    public Alert heading(final int size, final String heading) {
        this.headingSize = size;
        this.heading = heading;
        return this;
    }

    public Alert heading(final String heading) {
        this.heading = heading;
        return this;
    }

    public Alert info() {
        this.mode = "info";
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<div class=\"alert alert-").append(this.mode).append("\" role=\"alert\">");
        if (this.heading != null) {
            html.append("<h").append(this.headingSize).append(" class=\"alert-heading\">").append(this.heading).append("</h").append(this.headingSize).append(">");
        }
        this.pump.pump(html);
        html.append("</div>");
    }

    public Alert success() {
        this.mode = "success";
        return this;
    }

    public Alert warning() {
        this.mode = "warning";
        return this;
    }

}
