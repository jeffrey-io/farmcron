package farm.bsg.models;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.facebook.MessengerSend;
import farm.bsg.ops.CounterCodeGen;

public class SiteProperties extends RawObject {

    public static final ObjectSchema SCHEMA = ObjectSchema.singleton("site/", //
            Field.STRING("domain"), // i.e. bsg.farm (will be used once this data is global)
            Field.STRING("product_name").withDefault("Demo Site"), //

            Field.STRING("fb_page_token"), //

            Field.STRING("twilio_phone_number"), //
            Field.STRING("twilio_username"), //
            Field.STRING("twilio_password"), //

            Field.STRING("admin_phone"), // for critical system alerts

            Field.NUMBER("product_imaging_thumbprint_size").withDefault(120), //
            Field.NUMBER("product_imaging_normal_size").withDefault(400), //
            Field.STRING("description"),

            Field.STRING("business_hours"), // need a good way to edits
            Field.STRING("business_phone"), //
            Field.STRING("business_timezone"), //

            Field.STRING("fulfilment_strategy"), Field.NUMBER("delivery_radius"), // need a good way to edits
            Field.STRING("pickup_rule"), // (i) every tuesday at 2 pm for next 5 tuesdays; (ii) every business day at 4pm

            Field.STRING("business_address1"), //
            Field.STRING("business_address2"), //
            Field.STRING("business_city"), //
            Field.STRING("business_state"), //
            Field.STRING("business_postal") //
    );

    public static void link(final CounterCodeGen c) {
        c.section("Data: SiteProperties");
    }

    public SiteProperties() {
        super(SCHEMA);
    }

    TwilioRestClient getTwilioRestClient() {
        if (areAnyNull("twilio_username", "twilio_password")) {
            return null;
        }
        return new TwilioRestClient.Builder(get("twilio_username"), get("twilio_password")).build();
    }

    @Override
    protected void invalidateCache() {
    }

    public boolean notifyAdmin(final String message) {
        if (isNullOrEmpty("admin_phone")) {
            return false;
        }
        return sendTextMessage(get("admin_phone"), message);
    }

    public boolean sendFacebookMessage(final String userId, final String message) {
        try {
            if (isNullOrEmpty("fb_page_token")) {
                return false;
            }
            return new MessengerSend(get("fb_page_token")).send(userId, message);
        } catch (final Throwable t) {
            return false;
        }
    }

    public boolean sendTextMessage(final String to, final String message) {
        try {
            final TwilioRestClient rest = getTwilioRestClient();
            if (rest == null) {
                return false;
            }
            if (isNullOrEmpty("twilio_phone_number")) {
                return false;
            }
            Message.creator(new PhoneNumber(to), new PhoneNumber(get("twilio_phone_number")), message).create(rest);
            return true;
        } catch (final Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}
