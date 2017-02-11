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

import com.amazonaws.util.json.Jackson;

public class MessengerSend {

    private final String pageAccessToken;

    public MessengerSend(String pageAccessToken) {
        this.pageAccessToken = pageAccessToken;
    }

    public static byte[] executeJsonPost(String uri, String body) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                HttpPost post = new HttpPost(uri);
                post.setHeader("Content-Type", "application/json");
                post.setEntity(new StringEntity(body));
                CloseableHttpResponse response = httpclient.execute(post);
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream input = entity.getContent();
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] bytes = new byte[64 * 1024];
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
        } catch (Exception err) {
            return null;
        }
    }

    public void send(String userId, String text) {
        HashMap<String, Object> node = new HashMap<>();
        HashMap<String, Object> recipient = new HashMap<>();
        HashMap<String, Object> message = new HashMap<>();

        recipient.put("id", userId);
        message.put("text", text);
        node.put("recipient", recipient);
        node.put("message", message);

        String body = Jackson.toJsonString(node);
        String url = "https://graph.facebook.com/v2.6/me/messages?access_token=" + pageAccessToken;

        byte[] result = executeJsonPost(url, body);
        System.out.println(new String(result));
    }
}
