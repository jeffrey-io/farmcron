package farm.bsg.ops;

import java.util.ArrayList;

import org.slf4j.ILoggerFactory;

public class LogDatabase implements ILoggerFactory {
    private final ArrayList<String> lines;

    public LogDatabase() {
        this.lines = new ArrayList<>();
    }

    public synchronized void write(String level, String line) {
        lines.add(line);
    }

    @Override
    public org.slf4j.Logger getLogger(String name) {
        return new StreamLogger(name, this);
    }
    
    public void witnessThrowable(Throwable throwable) {
        
    }
    
    public synchronized void dump(StringBuilder sb) {
        for (String line : lines) {
            sb.append(line);
            sb.append("\n");
        }
    }
}
