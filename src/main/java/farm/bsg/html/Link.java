package farm.bsg.html;

import farm.bsg.route.FinishedHref;

public class Link extends HtmlPump {
    private final String   href;
    private final HtmlPump label;
    private String         clazz;
    private boolean        active;

    public Link(final String href, final HtmlPump label) {
        this.href = href;
        this.label = label;
        this.active = false;
    }

    public Link active(final boolean active) {
        this.active = active;
        return this;
    }

    public Link active_if_href_is(final FinishedHref currenfHref) {
        this.active = this.href.equals(currenfHref.value);
        return this;
    }

    @Deprecated
    public Link active_if_href_is(final String currenfHref) {
        this.active = this.href.equals(currenfHref);
        return this;
    }

    public Link btn_danger() {
        return clazz("btn btn-danger");
    }

    public Link btn_info() {
        return clazz("btn btn-info");
    }

    public Link btn_link() {
        return clazz("btn btn-link");
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

    public Link btn_warning() {
        return clazz("btn btn-warning");
    }

    public Link card_link() {
        return clazz("card-link");
    }

    public Link clazz(final String clazz) {
        this.clazz = clazz;
        return this;
    }

    public Link nav_link() {
        return clazz("nav-link");
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<a");
        if (this.clazz != null) {
            html.append(" class=\"").append(this.clazz);
            if (this.active) {
                html.append(" active");
            }
            html.append("\"");
        }
        if (this.href != null) {
            html.append(" href=\"").append(this.href).append("\"");
        }
        html.append(">");
        this.label.pump(html);
        html.append("</a>");
    }

}
