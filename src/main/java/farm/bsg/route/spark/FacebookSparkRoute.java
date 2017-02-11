package farm.bsg.route.spark;

import farm.bsg.facebook.AbstractFacebookHandler;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;
import spark.Request;
import spark.Response;
import spark.Route;

public class FacebookSparkRoute extends AbstractFacebookHandler implements Route {

    public FacebookSparkRoute(MultiTenantRouter router, RoutingTable routing) {
        super(router, routing);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        FacebookResponse fbResponse = facebookResponse( //
                request.headers("Host"), //
                request.body(), //
                new SparkBox(request, response, true));
        return fbResponse.httpBodyResponse;
    }
}
