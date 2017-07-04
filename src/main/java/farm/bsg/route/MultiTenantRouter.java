package farm.bsg.route;

import farm.bsg.ProductEngine;

public interface MultiTenantRouter {

    /**
     * find the given product by domain
     */
    public ProductEngine findByDomain(String domain);

    /**
     * Lazily inform the tenants when the routing table is built.
     */
    public void informRoutingTableBuilt(RoutingTable routing);

    /**
     * is the routing secure? are we are on HTTPS
     */
    public boolean isSecure();
}
