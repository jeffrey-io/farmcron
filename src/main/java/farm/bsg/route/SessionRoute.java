package farm.bsg.route;

/**
 * Bundles a request into an authorized session. In theory, if you have a SessionRequest available, then you are a valid user.
 *
 * @author jeffrey
 */
@FunctionalInterface
public interface SessionRoute {

    /**
     * handle the request
     */
    public Object handle(SessionRequest session);
}
