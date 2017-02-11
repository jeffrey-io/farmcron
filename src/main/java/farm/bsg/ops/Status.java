package farm.bsg.ops;

import org.slf4j.impl.StaticLoggerBinder;

import spark.Request;
import spark.Response;
import spark.Route;

public class Status implements Route {

    private final CounterSource source;
    private final LogDatabase   logs;

    public Status(CounterSource source) {
        this.source = source;
        this.logs = StaticLoggerBinder.getSingleton().database;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>Counters</h3>");
        sb.append(source.html());
        sb.append("<pre>");
        logs.dump(sb);
        sb.append("</pre>");
        return sb.toString();
    }

}
