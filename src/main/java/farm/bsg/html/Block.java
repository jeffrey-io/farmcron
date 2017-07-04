package farm.bsg.html;

import java.util.ArrayList;

public class Block extends HtmlPump {

    private final ArrayList<HtmlPump> pumps;

    public Block() {
        this.pumps = new ArrayList<>();
    }

    public Block add(final HtmlPump pump) {
        if (pump == null) {
            return null;
        }
        this.pumps.add(pump);
        return this;
    }

    public Block add(final String text) {
        if (text == null) {
            return null;
        }
        this.pumps.add(new Text(text));
        return this;
    }

    public Block add_if(final boolean condition, final HtmlPump pump) {
        if (condition && pump != null) {
            this.pumps.add(pump);
        }
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        for (final HtmlPump pump : this.pumps) {
            pump.pump(html);
        }
    }
}
