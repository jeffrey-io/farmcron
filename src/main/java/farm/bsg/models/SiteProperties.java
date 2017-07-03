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
            Field.STRING("description"));

    public SiteProperties() {
        super(SCHEMA);
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: SiteProperties");
    }

    @Override
    protected void invalidateCache() {
    }

    TwilioRestClient getTwilioRestClient() {
        if (areAnyNull("twilio_username", "twilio_password")) {
            return null;
        }
        return new TwilioRestClient.Builder(get("twilio_username"), get("twilio_password")).build();
    }

    public boolean notifyAdmin(String message) {
        if (isNullOrEmpty("admin_phone")) {
            return false;
        }
        return sendTextMessage(get("admin_phone"), message);
    }

    public boolean sendFacebookMessage(String userId, String message) {
        try {
            if (isNullOrEmpty("fb_page_token")) {
                return false;
            }
            return new MessengerSend(get("fb_page_token")).send(userId, message);
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean sendTextMessage(String to, String message) {
        try {
            TwilioRestClient rest = getTwilioRestClient();
            if (rest == null) {
                return false;
            }
            if (isNullOrEmpty("twilio_phone_number")) {
                return false;
            }
            Message.creator(new PhoneNumber(to), new PhoneNumber(get("twilio_phone_number")), message).create(rest);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}
