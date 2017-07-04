package farm.bsg;

import java.util.HashMap;

import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;

public class ManualRouter implements MultiTenantRouter {

    private HashMap<String, ProductEngine> engines;

    private ProductEngine                  engineWhenNotFound;
    private final boolean                  isSecure;

    public ManualRouter(boolean isSecure) {
        this.engines = new HashMap<>();
        this.engineWhenNotFound = null;
        this.isSecure = isSecure;
    }

    @Override
    public boolean isSecure() {
        return this.isSecure;
    }

    public void setDefault(ProductEngine engine) {
        this.engineWhenNotFound = engine;
    }

    public synchronized void addDomain(String domain, ProductEngine engine) {
        this.engines.put(domain, engine);
    }

    @Override
    public void informRoutingTableBuilt(RoutingTable routing) {
        if (this.engineWhenNotFound != null) {
            routing.flushNavbar(engineWhenNotFound.navbar);
        }
        for (ProductEngine engine : engines.values()) {
            routing.flushNavbar(engine.navbar);
        }
    }

    @Override
    public synchronized ProductEngine findByDomain(String domainRaw) {
        String domain = domainRaw.toLowerCase().trim();
        ProductEngine engine = engines.get(domain);
        if (engine == null) {
            engine = engineWhenNotFound;
        }
        return engine;
    }
}
