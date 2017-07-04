package farm.bsg.ops;

import org.slf4j.impl.StaticLoggerBinder;

import spark.Request;
import spark.Response;
import spark.Route;

public class Status implements Route {

    private final CounterSource source;
    private final LogDatabase   logs;

    public Status(final CounterSource source) {
        this.source = source;
        this.logs = StaticLoggerBinder.getSingleton().database;
    }

    @Override
    public Object handle(final Request request, final Response response) throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("<h3>Counters</h3>");
        sb.append(this.source.html());
        sb.append("<pre>");
        this.logs.dump(sb);
        sb.append("</pre>");
        return sb.toString();
    }

}
