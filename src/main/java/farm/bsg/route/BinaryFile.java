package farm.bsg.route;

public class BinaryFile {
    public final String filename;
    public final String contentType;
    public final byte[] bytes;

    public BinaryFile(final String filename, final String contentType, final byte[] bytes) {
        this.filename = filename;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("BinaryFile:").append(this.filename).append(" content-type:").append(this.contentType).toString();
    }
}
