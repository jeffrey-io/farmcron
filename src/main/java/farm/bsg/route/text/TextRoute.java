package farm.bsg.route.text;

import farm.bsg.ProductEngine;

/**
 * A route for handling text messages
 * 
 * @author jeffrey
 */
@FunctionalInterface
public interface TextRoute {
    public TextMessage handle(ProductEngine engine, TextMessage message);
}
