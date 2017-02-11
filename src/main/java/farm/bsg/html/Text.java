package farm.bsg.html;

public class Text extends HtmlPump {

    public String value;

    public Text(String value) {
        this.value = value;
    }

    @Override
    public void pump(StringBuilder html) {
        if (value == null) {
            return;
        }
        html.append(value);
    }

    public static Text EMPTY_TEXT = new Text("");
}
