package farm.bsg.facebook;

import java.util.ArrayList;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

import farm.bsg.route.text.TextMessage;

public class MessengerWebHookMessageRequest {

    public final boolean                valid;
    public final ArrayList<TextMessage> texts;

    public MessengerWebHookMessageRequest(String json) {
        JsonNode root = Jackson.fromJsonString(json, JsonNode.class);
        boolean valid_ = false;
        this.texts = new ArrayList<>();
        try {
            if ("page".equals(root.get("object").asText())) {
                JsonNode entryList = root.get("entry");
                if (entryList.isNull()) {
                    return;
                }
                for (int k = 0; k < entryList.size(); k++) {
                    JsonNode messagingText = entryList.get(k).get("messaging");
                    for (int j = 0; j < messagingText.size(); j++) {
                        TextMessage entry = parse(messagingText.get(j), json);
                        if (entry != null) {
                            this.texts.add(entry);
                        }
                    }
                }
                valid_ = true;
            }
        } finally {
            this.valid = valid_;
        }
    }

    public static TextMessage parse(JsonNode tree, String debug) {
        JsonNode sender = tree.get("sender");
        if (sender == null || sender.isNull() || !sender.isObject()) {
            return null;
        }
        JsonNode sender_id = sender.get("id");
        if (sender_id == null || sender_id.isNull() || sender_id.isObject() || sender_id.isArray()) {
            return null;
        }
        String from = sender_id.asText();

        JsonNode recipient = tree.get("recipient");
        if (recipient == null || recipient.isNull() || !recipient.isObject()) {
            return null;
        }
        JsonNode recipient_id = recipient.get("id");
        if (recipient_id == null || recipient_id.isNull() || recipient_id.isObject() || recipient_id.isArray()) {
            return null;
        }
        String to = recipient_id.asText();
        JsonNode message = tree.get("message");
        if (message == null || message.isNull() || !message.isObject()) {
            return null;
        }
        JsonNode message_text = message.get("text");
        if (message_text == null || message_text.isNull() || message_text.isObject() || message_text.isArray()) {
            return null;
        }
        String text = message_text.asText();

        return new TextMessage("facebook", to, from, text, debug);
    }
}
