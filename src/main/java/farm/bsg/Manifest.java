package farm.bsg;

import java.util.HashMap;

import com.amazonaws.util.json.Jackson;

import spark.Request;
import spark.Response;
import spark.Route;

public class Manifest implements Route {

    @Override
    public Object handle(final Request request, final Response response) throws Exception {
        final HashMap<String, Object> root = new HashMap<>();
        root.put("short_name", "BSG FarmCron");
        root.put("name", "BSG Farm Cron Tool");
        root.put("display", "fullscreen");
        root.put("orientation", "portrait");
        response.header("Content-type", "application/json");
        return Jackson.toJsonString(root);
    }

}
