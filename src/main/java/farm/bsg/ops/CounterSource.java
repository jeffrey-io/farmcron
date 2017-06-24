package farm.bsg.ops;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CounterSource {
    private final ArrayList<Counter>   counters;
    private final ArrayList<Histogram> histograms;
    private String                     section = "General";
    private AtomicBoolean              locked;

    public CounterSource() {
        this.counters = new ArrayList<>();
        this.histograms = new ArrayList<>();
        this.locked = new AtomicBoolean(false);
    }

    public void lockDown() {
        this.locked.set(true);
    }

    public void setSection(String section) {
        this.section = section;
    }

    public RequestMetrics request(String method, String uri) {
        return new RequestMetrics(this, method, uri);
    }

    public Counter counter(String name, String description) {
        if (locked.get()) {
            throw new RuntimeException("Unable to make counter; we are locked down");
        }
        Counter counter = new Counter(section, name, description, false);
        this.counters.add(counter);
        return counter;
    }

    public Histogram histogram(String name, String description) {
        if (locked.get()) {
            throw new RuntimeException("Unable to make counter; we are locked down");
        }
        Histogram histo = new Histogram(section, name, description);
        this.histograms.add(histo);
        return histo;
    }

    public Counter alarm(String name, String description) {
        if (locked.get()) {
            throw new RuntimeException("Unable to make counter; we are locked down");
        }
        Counter counter = new Counter(section, name, description, true);
        this.counters.add(counter);
        return counter;
    }

    public synchronized String html() {
        StringBuilder sb = new StringBuilder();
        String lastSection = "";
        sb.append("<h2>Counters</h2>");
        for (Counter counter : counters) {
            if (!lastSection.equals(counter.section)) {
                sb.append("<h3>").append(counter.section).append("</h3>");
                lastSection = counter.section;
            }
            sb.append(counter.name + " = " + counter.get() + "<br />");
        }
        lastSection = null;
        sb.append("<h2>Histograms</h2>");
        for (Histogram histo : histograms) {
            if (!lastSection.equals(histo.section)) {
                sb.append("<h3>").append(histo.section).append("</h3>");
                lastSection = histo.section;
            }
            sb.append(histo.name + " = " + histo.render() + "<br />");
        }
        return sb.toString();
    }
}
