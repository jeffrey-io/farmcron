package farm.bsg.html;

import java.util.ArrayList;

public class BreadCrumbs extends HtmlPump {

    private final ArrayList<HtmlPump> pumps;

    public BreadCrumbs() {
        this.pumps = new ArrayList<>();
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<ol class=\"breadcrumb\">");
        final int n = this.pumps.size();
        for (int k = 0; k < n; k++) {
            final HtmlPump pump = this.pumps.get(k);
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

    public BreadCrumbs with(final HtmlPump pump) {
        this.pumps.add(pump);
        return this;
    }

    public BreadCrumbs with(final String crumb) {
        return with(new Text(crumb));
    }

}
