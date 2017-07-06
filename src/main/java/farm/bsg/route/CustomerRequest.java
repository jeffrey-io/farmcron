package farm.bsg.route;

import java.util.UUID;

import farm.bsg.ProductEngine;
import farm.bsg.data.Authenticator.AuthResultCustomer;
import farm.bsg.models.Cart;
import farm.bsg.models.Customer;

public class CustomerRequest extends DelegateRequest {
    public static String       AUTH_COOKIE_NAME = "ca";

    public final ProductEngine engine;
    private String             cartId;
    public final boolean       hasCustomer;
    public final Customer      customer;

    /**
     *
     * @param engine
     *            the engine powering the data
     * @param delegate
     *            the request and response wrapper
     */
    public CustomerRequest(final ProductEngine engine, final RequestResponseWrapper delegate) {
        super(delegate);
        this.engine = engine;
        this.cartId = delegate.getCookie("ccid");
        final String cookie = delegate.getCookie(AUTH_COOKIE_NAME);
        final AuthResultCustomer auth = engine.auth.authenticateCustomerByCookies(cookie);
        this.hasCustomer = auth.allowed;
        this.customer = auth.customer;
        if (this.cartId == null) {
            generateNewCartId();
        } else if (auth.allowed && this.customer != null) {
            final Cart cart = this.engine.cart_by_id(this.cartId, false);
            if (cart != null) {
                final String whoOwnsCart = cart.get("customer");
                if (whoOwnsCart == null || "".equals(whoOwnsCart)) {
                    cart.set("customer", this.customer.getId());
                    engine.put(cart);
                } else if (!this.customer.getId().equals(whoOwnsCart)) {
                    generateNewCartId();
                }
            }
        }
    }

    public String generateNewCartId() {
        String gCartId = UUID.randomUUID().toString();
        while (this.engine.cart_by_id(gCartId, false) != null) {
            gCartId = UUID.randomUUID().toString();
        }
        setCookie("ccid", gCartId);
        this.cartId = gCartId;
        return gCartId;
    }

    public String getCartId() {
        return this.cartId;
    }
}
