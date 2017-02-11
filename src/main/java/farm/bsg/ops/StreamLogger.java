package farm.bsg.ops;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class StreamLogger extends MarkerIgnoringBase {
    private static final long serialVersionUID = 3939606563803299886L;

    public final String       name;

    public boolean            traceEnabled     = false;
    public boolean            debugEnabled     = false;
    public boolean            infoEnabled      = true;
    public boolean            warnEnabled      = true;
    public boolean            errorEnabled     = true;

    private final LogDatabase db;

    public StreamLogger(String name, LogDatabase db) {
        this.name = name;
        this.db = db;
    }

    /**
     * This is our internal implementation for logging regular (non-parameterized) log messages.
     *
     * @param level
     *            One of the LOG_LEVEL_XXX constants defining the log level
     * @param message
     *            The message itself
     * @param t
     *            The exception whose stack trace should be logged
     */
    private void log(String level, String message, Throwable t) {
        StringBuilder buf = new StringBuilder(32);
        buf.append(Long.toHexString(System.currentTimeMillis()));
        buf.append(" [");
        // Append current thread name if so configured
        buf.append(Thread.currentThread().getName());
        buf.append("] [");
        buf.append(level);
        buf.append("] ");
        buf.append(String.valueOf(name)).append(" - ");
        buf.append(message);
        if (t != null) {
            db.witnessThrowable(t);
            ByteArrayOutputStream memory = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(memory);
            t.printStackTrace(stream);

            buf.append("\n");
            buf.append(new String(memory.toByteArray()));
        }
        db.write(level, buf.toString());
    }

    public void log(LoggingEvent event) {
        if (event.getLevel() == Level.TRACE && !traceEnabled) {
            return;
        }
        if (event.getLevel() == Level.DEBUG && !debugEnabled) {
            return;
        }
        if (event.getLevel() == Level.INFO && !infoEnabled) {
            return;
        }
        if (event.getLevel() == Level.WARN && !warnEnabled) {
            return;
        }
        if (event.getLevel() == Level.ERROR && !errorEnabled) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(event.getMessage(), event.getArgumentArray(), event.getThrowable());
        log(event.getLevel().toString(), tp.getMessage(), event.getThrowable());
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arg1
     * @param arg2
     */
    private void formatAndLog(String level, String format, Object arg1, Object arg2) {
        FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arguments
     *            a list of 3 ore more arguments
     */
    private void formatAndLog(String level, String format, Object... arguments) {
        FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    /** Are {@code trace} messages currently enabled? */
    @Override
    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    /**
     * A simple implementation which logs messages of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(String msg) {
        if (traceEnabled) {
            log("TRACE", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(String format, Object param1) {
        if (traceEnabled) {
            formatAndLog("TRACE", format, param1, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(String format, Object param1, Object param2) {
        if (traceEnabled) {
            formatAndLog("TRACE", format, param1, param2);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(String format, Object... argArray) {
        if (traceEnabled) {
            formatAndLog("TRACE", format, argArray);
        }
    }

    /** Log a message of level TRACE, including an exception. */
    @Override
    public void trace(String msg, Throwable t) {
        if (traceEnabled) {
            log("TRACE", msg, t);
        }
    }

    /** Are {@code debug} messages currently enabled? */
    @Override
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * A simple implementation which logs messages of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(String msg) {
        if (debugEnabled) {
            log("DEBUG", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(String format, Object param1) {
        if (debugEnabled) {
            formatAndLog("DEBUG", format, param1, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(String format, Object param1, Object param2) {
        if (debugEnabled) {
            formatAndLog("DEBUG", format, param1, param2);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(String format, Object... argArray) {
        if (debugEnabled) {
            formatAndLog("DEBUG", format, argArray);
        }
    }

    /** Log a message of level DEBUG, including an exception. */
    @Override
    public void debug(String msg, Throwable t) {
        if (debugEnabled) {
            log("DEBUG", msg, t);
        }
    }

    /** Are {@code info} messages currently enabled? */
    @Override
    public boolean isInfoEnabled() {
        return infoEnabled;
    }

    /**
     * A simple implementation which logs messages of level INFO according to the format outlined above.
     */
    @Override
    public void info(String msg) {
        if (infoEnabled) {
            log("INFO", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level INFO according to the format outlined above.
     */
    @Override
    public void info(String format, Object arg) {
        if (infoEnabled) {
            formatAndLog("INFO", format, arg, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level INFO according to the format outlined above.
     */
    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (infoEnabled) {
            formatAndLog("INFO", format, arg1, arg2);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level INFO according to the format outlined above.
     */
    @Override
    public void info(String format, Object... argArray) {
        if (infoEnabled) {
            formatAndLog("INFO", format, argArray);
        }
    }

    /** Log a message of level INFO, including an exception. */
    @Override
    public void info(String msg, Throwable t) {
        if (infoEnabled) {
            log("INFO", msg, t);
        }
    }

    /** Are {@code warn} messages currently enabled? */
    @Override
    public boolean isWarnEnabled() {
        return warnEnabled;
    }

    /**
     * A simple implementation which always logs messages of level WARN according to the format outlined above.
     */
    @Override
    public void warn(String msg) {
        if (warnEnabled) {
            log("WARN", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level WARN according to the format outlined above.
     */
    @Override
    public void warn(String format, Object arg) {
        if (warnEnabled) {
            formatAndLog("WARN", format, arg, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level WARN according to the format outlined above.
     */
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (warnEnabled) {
            formatAndLog("WARN", format, arg1, arg2);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level WARN according to the format outlined above.
     */
    @Override
    public void warn(String format, Object... argArray) {
        if (warnEnabled) {
            formatAndLog("WARN", format, argArray);
        }
    }

    /** Log a message of level WARN, including an exception. */
    @Override
    public void warn(String msg, Throwable t) {
        if (warnEnabled) {
            log("WARN", msg, t);
        }
    }

    /** Are {@code error} messages currently enabled? */
    @Override
    public boolean isErrorEnabled() {
        return errorEnabled;
    }

    /**
     * A simple implementation which always logs messages of level ERROR according to the format outlined above.
     */
    @Override
    public void error(String msg) {
        if (errorEnabled) {
            log("ERROR", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level ERROR according to the format outlined above.
     */
    @Override
    public void error(String format, Object arg) {
        if (errorEnabled) {
            formatAndLog("ERROR", format, arg, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level ERROR according to the format outlined above.
     */
    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (errorEnabled) {
            formatAndLog("ERROR", format, arg1, arg2);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level ERROR according to the format outlined above.
     */
    @Override
    public void error(String format, Object... argArray) {
        if (errorEnabled) {
            formatAndLog("ERROR", format, argArray);
        }
    }

    /** Log a message of level ERROR, including an exception. */
    @Override
    public void error(String msg, Throwable t) {
        if (errorEnabled) {
            log("ERROR", msg, t);
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
