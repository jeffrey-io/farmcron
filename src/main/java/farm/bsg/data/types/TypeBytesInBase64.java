package farm.bsg.data.types;

import org.apache.commons.codec.binary.Base64;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;

public class TypeBytesInBase64 extends Type {

    public TypeBytesInBase64(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "bytes_base_64";
    }

    @Override
    public String normalize(String value) {
        return value;
    }

    @Override
    public boolean validate(String value) {
        return Base64.isBase64(value);
    }

    @Override
    public String defaultValue() {
        return null;
    }

    public static String project(ProjectionProvider provider, String key) {
        // TODO, need to see if I should do anything special for files here.... I should also encode it, here...
        return provider.first(key);
    }
}
