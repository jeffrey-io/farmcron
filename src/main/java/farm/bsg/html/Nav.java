package farm.bsg.html;

import java.util.ArrayList;

public class Nav extends HtmlPump {

    private final ArrayList<HtmlPump> pumps;
    private String                    extended_class;

    public Nav() {
        this.pumps = new ArrayList<>();
        this.extended_class = null;
    }

    public Nav pills() {
        this.extended_class = "nav-pills";
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<ul class=\"nav");
        if (this.extended_class != null) {
            html.append(" ").append(this.extended_class);
        }

        html.append("\">");
        for (final HtmlPump pump : this.pumps) {
            html.append("<li class=\"nav-item\">");
            pump.pump(html);
            html.append("</li>");
        }
        html.append("</ul>");
    }

    public Nav tabs() {
        this.extended_class = "nav-tabs";
        return this;
    }

    public Nav with(final HtmlPump pump) {
        this.pumps.add(pump);
        return this;
    }

    public Nav with_if(final boolean condition, final HtmlPump pump) {
        if (condition) {
            this.pumps.add(pump);
        }
        return this;
    }

}
