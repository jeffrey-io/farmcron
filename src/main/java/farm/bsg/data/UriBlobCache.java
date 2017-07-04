package farm.bsg.data;

import java.util.HashMap;
import java.util.function.Function;

import com.google.common.base.Charsets;

public class UriBlobCache {

    public static class UriBlob {
        public final String contentType;
        public final byte[] blob;

        public UriBlob(final String contentType, final byte[] blob) {
            this.contentType = contentType;
            this.blob = blob;
        }

        public UriBlob transform(final Function<String, String> fun) {
            final String output = fun.apply(new String(this.blob, Charsets.UTF_8));
            if (output == null) {
                return new UriBlob(this.contentType, new byte[0]);
            }
            return new UriBlob(this.contentType, output.getBytes(Charsets.UTF_8));
        }
    }

    private final HashMap<String, UriBlob> blobs;

    public UriBlobCache() {
        this.blobs = new HashMap<>();
    }

    public synchronized UriBlob get(final String uri) {
        return this.blobs.get(uri);
    }

    public synchronized void write(final String uri, final UriBlob blob) {
        this.blobs.put(uri, blob);
    }

}
