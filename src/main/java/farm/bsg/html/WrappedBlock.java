package farm.bsg.html;

import java.util.ArrayList;

public class WrappedBlock extends HtmlPump {

    private final ArrayList<HtmlPump> pumps;
    private String                    tag;
    private String                    clazz;

    public WrappedBlock() {
        this.pumps = new ArrayList<>();
        this.tag = "div";
        this.clazz = null;
    }

    public WrappedBlock card() {
        this.clazz = "card";
        return this;
    }

    public WrappedBlock card_text() {
        this.clazz = "card-text";
        return this;
    }

    public WrappedBlock card_title() {
        this.clazz = "card-title";
        return this;
    }

    public WrappedBlock form_group() {
        this.clazz = "form-group";
        return this;
    }

    public WrappedBlock form_small_heading() {
        this.clazz = "form-small-heading";
        return this;
    }

    public WrappedBlock h1() {
        this.tag = "h1";
        return this;
    }

    public WrappedBlock h2() {
        this.tag = "h2";
        return this;
    }

    public WrappedBlock h3() {
        this.tag = "h3";
        return this;
    }

    public WrappedBlock h4() {
        this.tag = "h4";
        return this;
    }

    public WrappedBlock h5() {
        this.tag = "h5";
        return this;
    }

    public WrappedBlock h6() {
        this.tag = "h6";
        return this;
    }

    public WrappedBlock li() {
        this.tag = "li";
        return this;
    }

    public WrappedBlock muted_form_text() {
        this.clazz = "form-text text-muted";
        return this;
    }

    public WrappedBlock ol() {
        this.tag = "ol";
        return this;
    }

    public WrappedBlock p() {
        this.tag = "p";
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<").append(this.tag);
        if (this.clazz != null) {
            html.append(" class=\"").append(this.clazz).append("\"");
        }
        html.append(">");
        for (final HtmlPump pump : this.pumps) {
            pump.pump(html);
        }
        html.append("</").append(this.tag).append(">");
    }

    public WrappedBlock small() {
        this.tag = "small";
        return this;
    }

    public WrappedBlock span() {
        this.tag = "span";
        return this;
    }

    public WrappedBlock strong() {
        this.tag = "strong";
        return this;
    }

    public WrappedBlock text_muted() {
        this.clazz = "text-muted";
        return this;
    }

    public WrappedBlock ul() {
        this.tag = "ul";
        return this;
    }

    public WrappedBlock wrap(final HtmlPump pump) {
        if (pump == null) {
            return this;
        }
        this.pumps.add(pump);
        return this;
    }

    public WrappedBlock wrap(final String str) {
        if (str == null) {
            return this;
        }
        this.pumps.add(new Text(str));
        return this;
    }

    public WrappedBlock wrap_if(final boolean condition, final HtmlPump pump) {
        if (condition && pump != null) {
            this.pumps.add(pump);
        }
        return this;
    }
}
