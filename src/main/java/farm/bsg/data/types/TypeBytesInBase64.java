package farm.bsg.data.types;

import org.apache.commons.codec.binary.Base64;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeBytesInBase64 extends Type {

    public static String project(final ProjectionProvider provider, final String key) {
        // TODO, need to see if I should do anything special for files here.... I should also encode it, here...
        return provider.first(key);
    }

    public TypeBytesInBase64(final String name) {
        super(name);
    }

    @Override
    public String defaultValue() {
        return null;
    }

    @Override
    public String normalize(final String value) {
        return value;
    }

    @Override
    public String type() {
        return "bytes_base_64";
    }

    @Override
    public boolean validate(final String value) {
        return Base64.isBase64(value);
    }
}
