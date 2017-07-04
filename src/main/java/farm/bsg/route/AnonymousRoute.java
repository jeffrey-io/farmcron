package farm.bsg.route;

/**
 * Bundles a request for an anonymous request; that is, we have no session. However, we _may_ have a session.
 *
 * @author jeffrey
 */
@FunctionalInterface
public interface AnonymousRoute {

    public Object handle(AnonymousRequest request);
}
