package farm.bsg.html;

public class Text extends HtmlPump {

    public static Text EMPTY_TEXT = new Text("");

    public String      value;

    public Text(final String value) {
        this.value = value;
    }

    @Override
    public void pump(final StringBuilder html) {
        if (this.value == null) {
            return;
        }
        html.append(this.value);
    }
}
