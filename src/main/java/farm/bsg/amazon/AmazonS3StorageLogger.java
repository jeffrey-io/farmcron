package farm.bsg.amazon;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import farm.bsg.data.Value;
import farm.bsg.data.contracts.KeyValueStoragePut;
import farm.bsg.data.contracts.PersistenceLogger;

public class AmazonS3StorageLogger implements PersistenceLogger {

    private final AmazonS3 s3;
    private final String   bucket;
    private final String   prefix;

    public AmazonS3StorageLogger(AmazonS3 s3, String bucket, String prefix) {
        this.s3 = s3;
        this.bucket = bucket;
        this.prefix = prefix;
    }

    @Override
    public boolean put(String key, Value newValue) {
        try {
            if (newValue == null) {
                s3.deleteObject(bucket, this.prefix + key);
            } else {
                s3.putObject(bucket, this.prefix + key, newValue.toString());
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public void pump(KeyValueStoragePut storage) throws Exception {
        // a failure here should terminate the process
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(this.bucket);
        request.setPrefix(this.prefix);
        int limit = 1000 * 1000;
        while (true) {
            ObjectListing listing = s3.listObjects(request);

            limit--;
            if (limit < 0) {
                throw new RuntimeException("Failed to start; over 1 million keys reached");
            }

            for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                String key = summary.getKey().substring(prefix.length());
                String value = s3.getObjectAsString(bucket, summary.getKey());
                storage.put(key, new Value(value));
            }
            if (!listing.isTruncated()) {
                return;
            }
            request.setMarker(listing.getNextMarker());
        }
    }
}
