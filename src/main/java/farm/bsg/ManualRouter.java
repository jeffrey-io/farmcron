package farm.bsg;

import java.util.HashMap;

import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;

public class ManualRouter implements MultiTenantRouter {

    private final HashMap<String, ProductEngine> engines;

    private ProductEngine                        engineWhenNotFound;
    private final boolean                        isSecure;

    public ManualRouter(final boolean isSecure) {
        this.engines = new HashMap<>();
        this.engineWhenNotFound = null;
        this.isSecure = isSecure;
    }

    public synchronized void addDomain(final String domain, final ProductEngine engine) {
        this.engines.put(domain, engine);
    }

    @Override
    public synchronized ProductEngine findByDomain(final String domainRaw) {
        final String domain = domainRaw.toLowerCase().trim();
        ProductEngine engine = this.engines.get(domain);
        if (engine == null) {
            engine = this.engineWhenNotFound;
        }
        return engine;
    }

    @Override
    public void informRoutingTableBuilt(final RoutingTable routing) {
        if (this.engineWhenNotFound != null) {
            routing.flushNavbar(this.engineWhenNotFound.navbar);
        }
        for (final ProductEngine engine : this.engines.values()) {
            routing.flushNavbar(engine.navbar);
        }
    }

    @Override
    public boolean isSecure() {
        return this.isSecure;
    }

    public void setDefault(final ProductEngine engine) {
        this.engineWhenNotFound = engine;
    }
}
