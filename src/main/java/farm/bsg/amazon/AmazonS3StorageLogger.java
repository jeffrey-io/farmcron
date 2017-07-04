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

    public AmazonS3StorageLogger(final AmazonS3 s3, final String bucket, final String prefix) {
        this.s3 = s3;
        this.bucket = bucket;
        this.prefix = prefix;
    }

    @Override
    public void pump(final KeyValueStoragePut storage) throws Exception {
        // a failure here should terminate the process
        final ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(this.bucket);
        request.setPrefix(this.prefix);
        int limit = 1000 * 1000;
        while (true) {
            final ObjectListing listing = this.s3.listObjects(request);

            limit--;
            if (limit < 0) {
                throw new RuntimeException("Failed to start; over 1 million keys reached");
            }

            for (final S3ObjectSummary summary : listing.getObjectSummaries()) {
                final String key = summary.getKey().substring(this.prefix.length());
                final String value = this.s3.getObjectAsString(this.bucket, summary.getKey());
                storage.put(key, new Value(value));
            }
            if (!listing.isTruncated()) {
                return;
            }
            request.setMarker(listing.getNextMarker());
        }
    }

    @Override
    public boolean put(final String key, final Value newValue) {
        try {
            if (newValue == null) {
                this.s3.deleteObject(this.bucket, this.prefix + key);
            } else {
                this.s3.putObject(this.bucket, this.prefix + key, newValue.toString());
            }
            return true;
        } catch (final Exception err) {
            return false;
        }
    }
}
