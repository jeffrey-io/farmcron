package farm.bsg.ops;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CounterSource {
    private final ArrayList<Counter>   counters;
    private final ArrayList<Histogram> histograms;
    private String                     section = "General";
    private final AtomicBoolean        locked;

    public CounterSource() {
        this.counters = new ArrayList<>();
        this.histograms = new ArrayList<>();
        this.locked = new AtomicBoolean(false);
    }

    public Counter alarm(final String name, final String description) {
        if (this.locked.get()) {
            throw new RuntimeException("Unable to make counter; we are locked down");
        }
        final Counter counter = new Counter(this.section, name, description, true);
        this.counters.add(counter);
        return counter;
    }

    public Counter counter(final String name, final String description) {
        if (this.locked.get()) {
            throw new RuntimeException("Unable to make counter; we are locked down");
        }
        final Counter counter = new Counter(this.section, name, description, false);
        this.counters.add(counter);
        return counter;
    }

    public Histogram histogram(final String name, final String description) {
        if (this.locked.get()) {
            throw new RuntimeException("Unable to make counter; we are locked down");
        }
        final Histogram histo = new Histogram(this.section, name, description);
        this.histograms.add(histo);
        return histo;
    }

    public synchronized String html() {
        final StringBuilder sb = new StringBuilder();
        String lastSection = "";
        sb.append("<h2>Counters</h2>");
        for (final Counter counter : this.counters) {
            if (!lastSection.equals(counter.section)) {
                sb.append("<h3>").append(counter.section).append("</h3>");
                lastSection = counter.section;
            }
            sb.append(counter.name + " = " + counter.get() + "<br />");
        }
        lastSection = "";
        sb.append("<h2>Histograms</h2>");
        for (final Histogram histo : this.histograms) {
            if (!lastSection.equals(histo.section)) {
                sb.append("<h3>").append(histo.section).append("</h3>");
                lastSection = histo.section;
            }
            sb.append(histo.name + " = " + histo.render() + "<br />");
        }
        return sb.toString();
    }

    public void lockDown() {
        this.locked.set(true);
    }

    public RequestMetrics request(final String method, final String uri) {
        return new RequestMetrics(this, method, uri);
    }

    public void setSection(final String section) {
        this.section = section;
    }
}
