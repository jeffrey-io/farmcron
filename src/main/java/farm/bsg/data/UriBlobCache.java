package farm.bsg.data;

import java.util.HashMap;
import java.util.function.Function;

import com.google.common.base.Charsets;


public class UriBlobCache {

    public static class UriBlob {
        public final String contentType;
        public final byte[] blob;

        public UriBlob(String contentType, byte[] blob) {
            this.contentType = contentType;
            this.blob = blob;
        }

        public UriBlob transform(Function<String, String> fun) {
            String output = fun.apply(new String(blob, Charsets.UTF_8));
            if (output == null) {
                return new UriBlob(contentType, new byte[0]);
            }
            return new UriBlob(contentType, output.getBytes(Charsets.UTF_8));
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
