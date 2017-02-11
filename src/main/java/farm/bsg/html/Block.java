package farm.bsg.html;

import java.util.ArrayList;

public class Block extends HtmlPump {

    private ArrayList<HtmlPump> pumps;

    public Block() {
        this.pumps = new ArrayList<>();
    }

    @Override
    public void pump(StringBuilder html) {
        for (HtmlPump pump : pumps) {
            pump.pump(html);
        }
    }

    public Block add(HtmlPump pump) {
        if (pump == null) {
            return null;
        }
        pumps.add(pump);
        return this;
    }

    public Block add(String text) {
        if (text == null) {
            return null;
        }
        pumps.add(new Text(text));
        return this;
    }

    public Block add_if(boolean condition, HtmlPump pump) {
        if (condition && pump != null) {
            pumps.add(pump);
        }
        return this;
    }
}
