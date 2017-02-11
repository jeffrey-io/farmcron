package farm.bsg.ops;

import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

public class Logs {

    public static Logger of(Class<?> clazz) {
        return StaticLoggerBinder.getSingleton().database.getLogger(clazz.getSimpleName());
    }
}
