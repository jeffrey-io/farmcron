package farm.bsg.html;

public abstract class HtmlPump {

    public abstract void pump(StringBuilder html);
    
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        pump(sb);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return toHtml();
    }
}
