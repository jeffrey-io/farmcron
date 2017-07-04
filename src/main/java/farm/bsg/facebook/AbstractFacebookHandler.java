package farm.bsg.facebook;

import farm.bsg.BsgCounters;
import farm.bsg.ProductEngine;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RequestResponseWrapper;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.text.TextMessage;

public class AbstractFacebookHandler {

    public static class FacebookResponse {
        public String httpBodyResponse;

        public FacebookResponse() {
            this.httpBodyResponse = "";
        }

        public FacebookResponse(final String httpBodyResponse) {
            this.httpBodyResponse = httpBodyResponse;
        }
    }

    private final MultiTenantRouter router;

    private final RoutingTable      routing;

    public AbstractFacebookHandler(final MultiTenantRouter router, final RoutingTable routing) {
        this.router = router;
        this.routing = routing;
    }

    private void dispatch(final ProductEngine engine, final TextMessage text) {
        final TextMessage response = this.routing.handleText(engine, text);
        BsgCounters.I.fb_attempt_response.bump();

        final String fbToken = engine.siteproperties_get().get("fb_page_token");
        if (fbToken != null) {
            BsgCounters.I.fb_send_out_on_wire.bump();
            final MessengerSend sender = new MessengerSend(fbToken);
            try {
                sender.send(response.to, response.message);
                BsgCounters.I.fb_send_ok.bump();
            } catch (final Exception err) {
                BsgCounters.I.fb_send_failed_send.bump();
            }
        } else {
            BsgCounters.I.fb_send_failed_no_fb_token.bump();
        }
    }

    public FacebookResponse facebookResponse(final String host, final String body, final RequestResponseWrapper req) {
        try {
            final ProductEngine engine = this.router.findByDomain(host);
            if (engine == null) {
                BsgCounters.I.fb_has_invalid_host.bump();
                return null;
            }

            // maybe look up the engine here
            final String mode = req.getParam("hub.mode");
            if ("subscribe".equals(mode)) {
                BsgCounters.I.fb_subscribe_begin.bump();
                final String challenge = req.getParam("hub.challenge");
                final String token = req.getParam("hub.verify_token");
                if ("linked".equals(token)) {
                    BsgCounters.I.fb_token_given_correct.bump();
                    return new FacebookResponse(challenge);
                }
                BsgCounters.I.fb_token_given_wrong.bump();
                return new FacebookResponse();
            }

            BsgCounters.I.fb_message.bump();
            final MessengerWebHookMessageRequest request = new MessengerWebHookMessageRequest(body);
            if (request.valid) {
                BsgCounters.I.fb_message_valid.bump();
                for (final TextMessage message : request.texts) {
                    dispatch(engine, message);
                }
            } else {
                BsgCounters.I.fb_message_invalid.bump();
            }
            // status.lastFacebookMessage.set(body);
        } catch (final Exception err) {
            err.printStackTrace();
        }
        return new FacebookResponse();
    }

}
