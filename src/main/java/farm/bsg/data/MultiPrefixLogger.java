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

    public <T extends KeyValuePairLogger> T add(String prefix, T logger) {
        ArrayList<KeyValuePairLogger> prefixLoggers = loggers.get(prefix);
        if (prefixLoggers == null) {
            prefixLoggers = new ArrayList<>();
            loggers.put(prefix, prefixLoggers);
        }
        prefixLoggers.add(logger);
        return logger;
    }

    @Override
    public void validate(String key, Value oldValue, Value newValue, PutResult result) {
        for (Entry<String, ArrayList<KeyValuePairLogger>> entry : loggers.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                for (KeyValuePairLogger logger : entry.getValue()) {
                    logger.validate(key, oldValue, newValue, result);
                }
            }
        }
    }

    @Override
    public void put(String key, Value oldValue, Value newValue) {
        for (Entry<String, ArrayList<KeyValuePairLogger>> entry : loggers.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                for (KeyValuePairLogger logger : entry.getValue()) {
                    logger.put(key, oldValue, newValue);
                }
            }
        }
    }

}
