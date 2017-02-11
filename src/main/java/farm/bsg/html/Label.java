package farm.bsg.html;

public class Label extends HtmlPump {

    private final String id;
    private HtmlPump     label;

    public Label(String id, HtmlPump label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<label for=\"").append(id).append("\">");
        label.pump(html);
        html.append("</label>");
    }
}
