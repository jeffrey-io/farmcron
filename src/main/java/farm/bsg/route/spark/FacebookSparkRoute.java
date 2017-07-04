package farm.bsg.route.spark;

import farm.bsg.facebook.AbstractFacebookHandler;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;
import spark.Request;
import spark.Response;
import spark.Route;

public class FacebookSparkRoute extends AbstractFacebookHandler implements Route {

    public FacebookSparkRoute(final MultiTenantRouter router, final RoutingTable routing) {
        super(router, routing);
    }

    @Override
    public Object handle(final Request request, final Response response) throws Exception {
        final FacebookResponse fbResponse = facebookResponse( //
                request.headers("Host"), //
                request.body(), //
                new SparkBox(request, response, true));
        return fbResponse.httpBodyResponse;
    }
}
