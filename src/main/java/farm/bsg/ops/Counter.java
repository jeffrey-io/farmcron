package farm.bsg.ops;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
    private final AtomicLong value;
    public final String      section;
    public final String      name;
    public final String      description;
    public final boolean     alarm;

    public Counter(final String section, final String name, final String description, boolean alarm) {
        this.value = new AtomicLong(0);
        this.section = section;
        this.name = name;
        this.description = description;
        this.alarm = alarm;
    }

    public void add(long delta) {
        this.value.addAndGet(delta);
    }

    public void bump() {
        value.incrementAndGet();
    }

    public void decrement() {
        value.decrementAndGet();
    }

    public long get() {
        return value.get();
    }

}
