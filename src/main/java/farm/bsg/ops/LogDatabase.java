package farm.bsg.ops;

import java.util.ArrayList;

import org.slf4j.ILoggerFactory;

public class LogDatabase implements ILoggerFactory {
    private final ArrayList<String> lines;

    public LogDatabase() {
        this.lines = new ArrayList<>();
    }

    public synchronized void dump(final StringBuilder sb) {
        for (final String line : this.lines) {
            sb.append(line);
            sb.append("\n");
        }
    }

    @Override
    public org.slf4j.Logger getLogger(final String name) {
        return new StreamLogger(name, this);
    }

    public void witnessThrowable(final Throwable throwable) {

    }

    public synchronized void write(final String level, final String line) {
        this.lines.add(line);
    }
}
