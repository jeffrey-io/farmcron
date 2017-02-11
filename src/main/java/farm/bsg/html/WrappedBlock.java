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

    public WrappedBlock wrap_if(boolean condition, HtmlPump pump) {
        if (condition && pump != null) {
            pumps.add(pump);
        }
        return this;
    }

    public WrappedBlock wrap(HtmlPump pump) {
        if (pump == null) {
            return this;
        }
        pumps.add(pump);
        return this;
    }

    public WrappedBlock wrap(String str) {
        if (str == null) {
            return this;
        }
        pumps.add(new Text(str));
        return this;
    }

    public WrappedBlock card() {
        clazz = "card";
        return this;
    }

    public WrappedBlock form_group() {
        clazz = "form-group";
        return this;
    }

    public WrappedBlock small() {
        tag = "small";
        return this;
    }

    public WrappedBlock muted_form_text() {
        clazz = "form-text text-muted";
        return this;
    }

    public WrappedBlock text_muted() {
        clazz = "text-muted";
        return this;
    }

    public WrappedBlock card_title() {
        clazz = "card-title";
        return this;
    }

    public WrappedBlock card_text() {
        clazz = "card-text";
        return this;
    }

    public WrappedBlock h1() {
        tag = "h1";
        return this;
    }

    public WrappedBlock li() {
        tag = "li";
        return this;
    }

    public WrappedBlock ul() {
        tag = "ul";
        return this;
    }

    public WrappedBlock ol() {
        tag = "ol";
        return this;
    }

    public WrappedBlock h2() {
        tag = "h2";
        return this;
    }

    public WrappedBlock h3() {
        tag = "h3";
        return this;
    }

    public WrappedBlock h4() {
        tag = "h4";
        return this;
    }

    public WrappedBlock h5() {
        tag = "h5";
        return this;
    }

    public WrappedBlock h6() {
        tag = "h6";
        return this;
    }

    public WrappedBlock p() {
        tag = "p";
        return this;
    }

    public WrappedBlock span() {
        tag = "span";
        return this;
    }

    public WrappedBlock strong() {
        tag = "strong";
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<").append(tag);
        if (clazz != null) {
            html.append(" class=\"").append(clazz).append("\"");
        }
        html.append(">");
        for (HtmlPump pump : pumps) {
            pump.pump(html);
        }
        html.append("</").append(tag).append(">");
    }
}
