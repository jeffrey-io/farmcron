package farm.bsg.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import farm.bsg.data.contracts.KeyValuePairLogger;

public class MultiPrefixLogger implements KeyValuePairLogger {

    private final HashMap<String, ArrayList<KeyValuePairLogger>> loggers;

    public MultiPrefixLogger() {
        this.loggers = new HashMap<>();
    }

    public <T extends KeyValuePairLogger> T add(final String prefix, final T logger) {
        ArrayList<KeyValuePairLogger> prefixLoggers = this.loggers.get(prefix);
        if (prefixLoggers == null) {
            prefixLoggers = new ArrayList<>();
            this.loggers.put(prefix, prefixLoggers);
        }
        prefixLoggers.add(logger);
        return logger;
    }

    @Override
    public void put(final String key, final Value oldValue, final Value newValue) {
        for (final Entry<String, ArrayList<KeyValuePairLogger>> entry : this.loggers.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                for (final KeyValuePairLogger logger : entry.getValue()) {
                    logger.put(key, oldValue, newValue);
                }
            }
        }
    }

    @Override
    public void validate(final String key, final Value oldValue, final Value newValue, final PutResult result) {
        for (final Entry<String, ArrayList<KeyValuePairLogger>> entry : this.loggers.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                for (final KeyValuePairLogger logger : entry.getValue()) {
                    logger.validate(key, oldValue, newValue, result);
                }
            }
        }
    }

}
