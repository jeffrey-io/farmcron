package farm.bsg.route.text;

import farm.bsg.Security.Permission;

/**
 * Represents a text message on a source service (i.e. sms, facebook, etc...), who the message was meant for, who the messsage was from, the message contents, and then any debug information which would be useful to keep
 *
 * @author jeffrey
 *
 */
public class TextMessage {
    public final String  source;
    public final String  to;
    public final String  from;
    public final String  message;
    public final String  debug;
    public final boolean origin;

    public TextMessage(final String source, final String to, final String from, final String message, final String debug) {
        this(source, to, from, message, debug, true);
    }

    private TextMessage(final String source, final String to, final String from, final String message, final String debug, final boolean origin) {
        this.source = source;
        this.to = to;
        this.from = from;
        this.message = message;
        this.debug = debug;
        this.origin = origin;
    }

    public TextMessage generateResponse(final String response) {
        return new TextMessage(this.source, this.from, this.to, response, this.debug, false);
    }

    public String getNotificationUri() {
        if (!this.origin) {
            throw new AssertionError("unable to generate notification uri from a non-origin message");
        }
        return this.source + ":" + this.from;
    }

    public boolean has(final Permission permission) {
        return false;
    }
}
