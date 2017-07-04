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

    public StreamLogger(final String name, final LogDatabase db) {
        this.name = name;
        this.db = db;
    }

    /**
     * A simple implementation which logs messages of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(final String msg) {
        if (this.debugEnabled) {
            log("DEBUG", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(final String format, final Object param1) {
        if (this.debugEnabled) {
            formatAndLog("DEBUG", format, param1, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(final String format, final Object... argArray) {
        if (this.debugEnabled) {
            formatAndLog("DEBUG", format, argArray);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level DEBUG according to the format outlined above.
     */
    @Override
    public void debug(final String format, final Object param1, final Object param2) {
        if (this.debugEnabled) {
            formatAndLog("DEBUG", format, param1, param2);
        }
    }

    /** Log a message of level DEBUG, including an exception. */
    @Override
    public void debug(final String msg, final Throwable t) {
        if (this.debugEnabled) {
            log("DEBUG", msg, t);
        }
    }

    /**
     * A simple implementation which always logs messages of level ERROR according to the format outlined above.
     */
    @Override
    public void error(final String msg) {
        if (this.errorEnabled) {
            log("ERROR", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level ERROR according to the format outlined above.
     */
    @Override
    public void error(final String format, final Object arg) {
        if (this.errorEnabled) {
            formatAndLog("ERROR", format, arg, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level ERROR according to the format outlined above.
     */
    @Override
    public void error(final String format, final Object... argArray) {
        if (this.errorEnabled) {
            formatAndLog("ERROR", format, argArray);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level ERROR according to the format outlined above.
     */
    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        if (this.errorEnabled) {
            formatAndLog("ERROR", format, arg1, arg2);
        }
    }

    /** Log a message of level ERROR, including an exception. */
    @Override
    public void error(final String msg, final Throwable t) {
        if (this.errorEnabled) {
            log("ERROR", msg, t);
        }
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arguments
     *            a list of 3 ore more arguments
     */
    private void formatAndLog(final String level, final String format, final Object... arguments) {
        final FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level
     * @param format
     * @param arg1
     * @param arg2
     */
    private void formatAndLog(final String level, final String format, final Object arg1, final Object arg2) {
        final FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * A simple implementation which logs messages of level INFO according to the format outlined above.
     */
    @Override
    public void info(final String msg) {
        if (this.infoEnabled) {
            log("INFO", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level INFO according to the format outlined above.
     */
    @Override
    public void info(final String format, final Object arg) {
        if (this.infoEnabled) {
            formatAndLog("INFO", format, arg, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level INFO according to the format outlined above.
     */
    @Override
    public void info(final String format, final Object... argArray) {
        if (this.infoEnabled) {
            formatAndLog("INFO", format, argArray);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level INFO according to the format outlined above.
     */
    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        if (this.infoEnabled) {
            formatAndLog("INFO", format, arg1, arg2);
        }
    }

    /** Log a message of level INFO, including an exception. */
    @Override
    public void info(final String msg, final Throwable t) {
        if (this.infoEnabled) {
            log("INFO", msg, t);
        }
    }

    /** Are {@code debug} messages currently enabled? */
    @Override
    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }

    /** Are {@code error} messages currently enabled? */
    @Override
    public boolean isErrorEnabled() {
        return this.errorEnabled;
    }

    /** Are {@code info} messages currently enabled? */
    @Override
    public boolean isInfoEnabled() {
        return this.infoEnabled;
    }

    /** Are {@code trace} messages currently enabled? */
    @Override
    public boolean isTraceEnabled() {
        return this.traceEnabled;
    }

    /** Are {@code warn} messages currently enabled? */
    @Override
    public boolean isWarnEnabled() {
        return this.warnEnabled;
    }

    public void log(final LoggingEvent event) {
        if (event.getLevel() == Level.TRACE && !this.traceEnabled) {
            return;
        }
        if (event.getLevel() == Level.DEBUG && !this.debugEnabled) {
            return;
        }
        if (event.getLevel() == Level.INFO && !this.infoEnabled) {
            return;
        }
        if (event.getLevel() == Level.WARN && !this.warnEnabled) {
            return;
        }
        if (event.getLevel() == Level.ERROR && !this.errorEnabled) {
            return;
        }
        final FormattingTuple tp = MessageFormatter.arrayFormat(event.getMessage(), event.getArgumentArray(), event.getThrowable());
        log(event.getLevel().toString(), tp.getMessage(), event.getThrowable());
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
    private void log(final String level, final String message, final Throwable t) {
        final StringBuilder buf = new StringBuilder(32);
        buf.append(Long.toHexString(System.currentTimeMillis()));
        buf.append(" [");
        // Append current thread name if so configured
        buf.append(Thread.currentThread().getName());
        buf.append("] [");
        buf.append(level);
        buf.append("] ");
        buf.append(String.valueOf(this.name)).append(" - ");
        buf.append(message);
        if (t != null) {
            this.db.witnessThrowable(t);
            final ByteArrayOutputStream memory = new ByteArrayOutputStream();
            final PrintStream stream = new PrintStream(memory);
            t.printStackTrace(stream);

            buf.append("\n");
            buf.append(new String(memory.toByteArray()));
        }
        this.db.write(level, buf.toString());
    }

    /**
     * A simple implementation which logs messages of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(final String msg) {
        if (this.traceEnabled) {
            log("TRACE", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(final String format, final Object param1) {
        if (this.traceEnabled) {
            formatAndLog("TRACE", format, param1, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(final String format, final Object... argArray) {
        if (this.traceEnabled) {
            formatAndLog("TRACE", format, argArray);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level TRACE according to the format outlined above.
     */
    @Override
    public void trace(final String format, final Object param1, final Object param2) {
        if (this.traceEnabled) {
            formatAndLog("TRACE", format, param1, param2);
        }
    }

    /** Log a message of level TRACE, including an exception. */
    @Override
    public void trace(final String msg, final Throwable t) {
        if (this.traceEnabled) {
            log("TRACE", msg, t);
        }
    }

    /**
     * A simple implementation which always logs messages of level WARN according to the format outlined above.
     */
    @Override
    public void warn(final String msg) {
        if (this.warnEnabled) {
            log("WARN", msg, null);
        }
    }

    /**
     * Perform single parameter substitution before logging the message of level WARN according to the format outlined above.
     */
    @Override
    public void warn(final String format, final Object arg) {
        if (this.warnEnabled) {
            formatAndLog("WARN", format, arg, null);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level WARN according to the format outlined above.
     */
    @Override
    public void warn(final String format, final Object... argArray) {
        if (this.warnEnabled) {
            formatAndLog("WARN", format, argArray);
        }
    }

    /**
     * Perform double parameter substitution before logging the message of level WARN according to the format outlined above.
     */
    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        if (this.warnEnabled) {
            formatAndLog("WARN", format, arg1, arg2);
        }
    }

    /** Log a message of level WARN, including an exception. */
    @Override
    public void warn(final String msg, final Throwable t) {
        if (this.warnEnabled) {
            log("WARN", msg, t);
        }
    }

}
