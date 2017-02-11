package farm.bsg.html;

public class Alert extends HtmlPump {

    private final HtmlPump pump;
    private String         mode;
    private int            headingSize = 4;
    private String         heading;

    public Alert(HtmlPump pump) {
        this.pump = pump;
        this.mode = "info";
        this.heading = null;
    }

    public Alert info() {
        this.mode = "info";
        return this;
    }

    public Alert success() {
        this.mode = "success";
        return this;
    }

    public Alert warning() {
        this.mode = "warning";
        return this;
    }

    public Alert danger() {
        this.mode = "danger";
        return this;
    }

    public Alert heading(String heading) {
        this.heading = heading;
        return this;
    }

    public Alert heading(int size, String heading) {
        this.headingSize = size;
        this.heading = heading;
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<div class=\"alert alert-").append(mode).append("\" role=\"alert\">");
        if (heading != null) {
            html.append("<h").append(headingSize).append(" class=\"alert-heading\">").append(heading).append("</h").append(headingSize).append(">");
        }
        pump.pump(html);
        html.append("</div>");
    }

}
