package farm.bsg.route;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Charsets;

public class SimpleURI extends ControlledURI {

    private final String uri;

    public SimpleURI(final String uri) {
        this.uri = uri;
    }

    @Override
    public FinishedHref href(final Map<String, String> map) {
        if (map.size() > 0) {
            final ArrayList<NameValuePair> pairs = new ArrayList<>();
            for (final Entry<String, String> entry : map.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            return new FinishedHref(this.uri + "?" + URLEncodedUtils.format(pairs, Charsets.UTF_8));
        } else {
            return new FinishedHref(this.uri);
        }
    }

    @Override
    public String toRoutingPattern() {
        return this.uri;
    }

    @Override
    public String toString() {
        return "Simple:" + toRoutingPattern();
    }
}
