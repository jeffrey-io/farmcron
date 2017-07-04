package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class WakeInputFile extends RawObject {

    public static ObjectSchema SCHEMA = ObjectSchema.persisted("wake_input/", //
            Field.STRING("filename").makeIndex(true), // -
            Field.STRING("content_type"), // -
            Field.STRING("description"), // -
            Field.BYTESB64("contents") // -
    ).dirty("farm.bsg.models.PublicSiteBuilder");

    public static void link(final CounterCodeGen c) {
        c.section("Data: Wake Input File");
        c.counter("compile_wake", "wake files are being compiled");
        c.counter("wake_file_written_blob_cache", "a file was generated and put in the blob cache");
    }

    public WakeInputFile() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

    public boolean isImage() {
        final String contentType = get("content_type");
        if ("image/jpeg".equals(contentType)) {
            return true;
        }
        if ("image/png".equals(contentType)) {
            return true;
        }
        return false;
    }
}
