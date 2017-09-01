package farm.bsg.route;

@FunctionalInterface
public interface ApiAction {
    public Object handle(ApiRequest request);
}
