package farm.bsg.ops;

import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

public class Logs {

    public static Logger of(final Class<?> clazz) {
        return StaticLoggerBinder.getSingleton().database.getLogger(clazz.getSimpleName());
    }

    public static Logger of(final Class<?> clazz, final String suffix) {
        return StaticLoggerBinder.getSingleton().database.getLogger(clazz.getSimpleName() + ":" + suffix);
    }
}
