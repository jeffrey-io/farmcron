package farm.bsg.ops;

import java.util.ArrayList;

import com.amazonaws.util.json.Jackson;

public class Histogram {
    private ArrayList<Long> samples;
    public final String      section;
    public final String      name;
    public final String      description;

    public Histogram(final String section, final String name, final String description) {
        samples = new ArrayList<>();
        this.section = section;
        this.name = name;
        this.description = description;
    }    
    
    public synchronized void add(long sample) {
        samples.add(sample);
    }
    
    public synchronized String render() {
        return Jackson.toJsonPrettyString(samples);
    }
}
