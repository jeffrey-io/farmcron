package farm.bsg.data;

import java.util.HashMap;

public class UriBlobCache {

    public static class UriBlob {
        public final String contentType;
        public final byte[] blob;

        public UriBlob(String contentType, byte[] blob) {
            this.contentType = contentType;
            this.blob = blob;
        }
    }

    private final HashMap<String, UriBlob> blobs;

    public UriBlobCache() {
        this.blobs = new HashMap<>();
    }

    public synchronized void write(String uri, UriBlob blob) {
        blobs.put(uri, blob);
    }

    public synchronized UriBlob get(String uri) {
        return blobs.get(uri);
    }
}
