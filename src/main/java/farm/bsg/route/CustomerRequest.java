package farm.bsg.route;

import java.util.UUID;

import farm.bsg.ProductEngine;

public class CustomerRequest extends DelegateRequest {
    public final ProductEngine engine;
    private String cartId;

    /**
     * 
     * @param engine
     *            the engine powering the data
     * @param delegate
     *            the request and response wrapper
     */
    public CustomerRequest(ProductEngine engine, RequestResponseWrapper delegate) {
        super(delegate);
        this.engine = engine;
        this.cartId = delegate.getCookie("ccid");
        if (cartId == null) {
            generateNewCartId();
        }
    }
    
    public String getCartId() {
        return cartId;
    }
    
    public String generateNewCartId() {
        String gCartId = UUID.randomUUID().toString();
        while (engine.cart_by_id(gCartId, false) != null) {
            gCartId = UUID.randomUUID().toString();
        }
        setCookie("ccid", gCartId);
        this.cartId = gCartId;
        return gCartId;
    }
}
