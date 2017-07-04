package farm.bsg.ops;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
    private final AtomicLong value;
    public final String      section;
    public final String      name;
    public final String      description;
    public final boolean     alarm;

    public Counter(final String section, final String name, final String description, final boolean alarm) {
        this.value = new AtomicLong(0);
        this.section = section;
        this.name = name;
        this.description = description;
        this.alarm = alarm;
    }

    public void add(final long delta) {
        this.value.addAndGet(delta);
    }

    public void bump() {
        this.value.incrementAndGet();
    }

    public void decrement() {
        this.value.decrementAndGet();
    }

    public long get() {
        return this.value.get();
    }

}
