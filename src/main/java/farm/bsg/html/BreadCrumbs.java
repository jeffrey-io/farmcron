package farm.bsg.html;

import java.util.ArrayList;

public class BreadCrumbs extends HtmlPump {

    private final ArrayList<HtmlPump> pumps;

    public BreadCrumbs() {
        this.pumps = new ArrayList<>();
    }

    public BreadCrumbs with(HtmlPump pump) {
        this.pumps.add(pump);
        return this;
    }

    public BreadCrumbs with(String crumb) {
        return with(new Text(crumb));
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<ol class=\"breadcrumb\">");
        int n = pumps.size();
        for (int k = 0; k < n; k++) {
            HtmlPump pump = pumps.get(k);
            if (k + 1 < n) {
                html.append("<li class=\"breadcrumb-item\">");
            } else {
                html.append("<li class=\"breadcrumb-item active\">");
            }
            pump.pump(html);
            html.append("</li>");
        }
        html.append("</ol>");
    }

}
