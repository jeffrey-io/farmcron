package farm.bsg.pages.common;

import java.util.ArrayList;
import java.util.HashMap;

import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.route.SessionRequest;

// TODO: migrate a bunch of this to farm.bsg.html
public class NavBar {

    private class NavItem {
        public final String     href;
        public final String     label;
        public final Permission permission;

        public NavItem(final String href, final String label, final Permission permission) {
            this.href = href;
            this.label = label;
            this.permission = permission;
        }
    }

    private final ArrayList<NavItem>      items;
    private final ProductEngine           engine;
    private final HashMap<String, String> uri2title;

    public NavBar(final ProductEngine engine) {
        this.engine = engine;
        this.items = new ArrayList<>();
        this.uri2title = new HashMap<>();
    }

    public void add(final String href, final String label, final Permission permission) {
        this.uri2title.put(href, label);
        this.items.add(new NavItem(href, label, permission));
    }

    public String html(final String href, final SessionRequest session) {
        final String productName = this.engine.siteproperties_get().get("product_name");
        final StringBuilder sb = new StringBuilder();
        sb.append("<nav class=\"navbar navbar-fixed-top navbar-dark bg-inverse\"><div class=\"container\">");
        sb.append("<button class=\"navbar-toggler hidden-sm-up\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbar-header\" aria-controls=\"navbar-header\" aria-expanded=\"false\" aria-label=\"Toggle navigation\"></button>");
        sb.append("<div class=\"collapse navbar-toggleable-xs\" id=\"navbar-header\">");
        sb.append("<a class=\"navbar-brand\" href=\"/\">" + productName + "</a>");
        sb.append("<ul class=\"nav navbar-nav\">");
        for (final NavItem item : this.items) {
            if (!session.has(item.permission)) {
                continue;
            }
            if (item.href.equals(href)) {
                sb.append("<li class=\"nav-item active\">");
                sb.append("<a class=\"nav-link\" href=\"" + item.href + "\">" + item.label + "<span class=\"sr-only\">(current)</span></a>");
                sb.append("</li>");
            } else {
                sb.append("<li class=\"nav-item\">");
                sb.append("<a class=\"nav-link\" href=\"" + item.href + "\">" + item.label + "</a>");
                sb.append("</li>");
            }
        }
        sb.append("</ul>");
        sb.append("</div>");
        sb.append("</div></nav>");
        return sb.toString();
    }

    public String title(final String href) {
        return this.uri2title.get(href);
    }
}
