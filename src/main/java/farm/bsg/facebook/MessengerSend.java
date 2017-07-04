package farm.bsg.facebook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import com.amazonaws.util.json.Jackson;

import farm.bsg.ops.Logs;

public class MessengerSend {

    private static final Logger LOG = Logs.of(MessengerSend.class);

    public static byte[] executeJsonPost(final String uri, final String body) {
        try {
            final CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                final HttpPost post = new HttpPost(uri);
                post.setHeader("Content-Type", "application/json");
                post.setEntity(new StringEntity(body));
                final CloseableHttpResponse response = httpclient.execute(post);
                try {
                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        final InputStream input = entity.getContent();
                        try {
                            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            final byte[] bytes = new byte[64 * 1024];
                            int read;
                            while ((read = input.read(bytes)) >= 0) {
                                baos.write(bytes, 0, read);
                            }
                            return baos.toByteArray();
                        } finally {
                            input.close();
                        }
                    }
                    return null;
                } finally {
                    response.close();
                    post.releaseConnection();
                }
            } finally {
                httpclient.close();
            }
        } catch (final Exception err) {
            return null;
        }
    }

    private final String pageAccessToken;

    public MessengerSend(final String pageAccessToken) {
        this.pageAccessToken = pageAccessToken;
    }

    public boolean send(final String userId, final String text) {
        final HashMap<String, Object> node = new HashMap<>();
        final HashMap<String, Object> recipient = new HashMap<>();
        final HashMap<String, Object> message = new HashMap<>();

        recipient.put("id", userId);
        message.put("text", text);
        node.put("recipient", recipient);
        node.put("message", message);

        final String body = Jackson.toJsonString(node);
        final String url = "https://graph.facebook.com/v2.6/me/messages?access_token=" + this.pageAccessToken;

        final byte[] result = executeJsonPost(url, body);
        LOG.info("Message-send-result-fb: {}", new String(result));
        return true;
    }
}
