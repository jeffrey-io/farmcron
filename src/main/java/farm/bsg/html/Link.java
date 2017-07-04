package farm.bsg.html;

import farm.bsg.route.FinishedHref;

public class Link extends HtmlPump {
    private String   href;
    private HtmlPump label;
    private String   clazz;
    private boolean  active;

    public Link(String href, HtmlPump label) {
        this.href = href;
        this.label = label;
        this.active = false;
    }

    public Link clazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public Link active(boolean active) {
        this.active = active;
        return this;
    }

    @Deprecated
    public Link active_if_href_is(String currenfHref) {
        this.active = href.equals(currenfHref);
        return this;
    }

    public Link active_if_href_is(FinishedHref currenfHref) {
        this.active = href.equals(currenfHref.value);
        return this;
    }

    public Link card_link() {
        return clazz("card-link");
    }

    public Link nav_link() {
        return clazz("nav-link");
    }

    public Link btn_primary() {
        return clazz("btn btn-primary");
    }

    public Link btn_secondary() {
        return clazz("btn btn-secondary");
    }

    public Link btn_success() {
        return clazz("btn btn-success");
    }

    public Link btn_info() {
        return clazz("btn btn-info");
    }

    public Link btn_warning() {
        return clazz("btn btn-warning");
    }

    public Link btn_danger() {
        return clazz("btn btn-danger");
    }

    public Link btn_link() {
        return clazz("btn btn-link");
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<a");
        if (clazz != null) {
            html.append(" class=\"").append(clazz);
            if (active) {
                html.append(" active");
            }
            html.append("\"");
        }
        if (href != null) {
            html.append(" href=\"").append(href).append("\"");
        }
        html.append(">");
        label.pump(html);
        html.append("</a>");
    }

}
