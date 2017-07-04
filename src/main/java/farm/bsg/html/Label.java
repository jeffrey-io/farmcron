package farm.bsg.html;

public class Label extends HtmlPump {

    private final String   id;
    private final HtmlPump label;

    public Label(final String id, final HtmlPump label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<label for=\"").append(this.id).append("\">");
        this.label.pump(html);
        html.append("</label>");
    }
}
