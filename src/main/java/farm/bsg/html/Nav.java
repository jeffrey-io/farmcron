package farm.bsg.html;

import java.util.ArrayList;

public class Nav extends HtmlPump {

    private final ArrayList<HtmlPump> pumps;
    private String                    extended_class;

    public Nav() {
        this.pumps = new ArrayList<>();
        this.extended_class = null;
    }

    public Nav with(HtmlPump pump) {
        this.pumps.add(pump);
        return this;
    }

    public Nav with_if(boolean condition, HtmlPump pump) {
        if (condition) {
            this.pumps.add(pump);
        }
        return this;
    }

    public Nav tabs() {
        this.extended_class = "nav-tabs";
        return this;
    }

    public Nav pills() {
        this.extended_class = "nav-pills";
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<ul class=\"nav");
        if (extended_class != null) {
            html.append(" ").append(extended_class);
        }

        html.append("\">");
        for (HtmlPump pump : pumps) {
            html.append("<li class=\"nav-item\">");
            pump.pump(html);
            html.append("</li>");
        }
        html.append("</ul>");
    }

}
