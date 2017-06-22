package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Product extends RawObject {

    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("product/", //
            Field.STRING("name"), // -
            Field.STRING("description"), // -
            Field.STRING("category"), // -
            Field.STRING("customizations"), // -
            Field.NUMBER("price"), //
            Field.BYTESB64("image"), // -
            Field.STRING("image_content_type"), // -
            Field.STRING("image_hash") // -
    ).dirty("farm.bsg.models.PublicSiteBuilder");

    public Product() {
        super(SCHEMA);
    }

    @Override
    protected void invalidateCache() {
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: Product");
    }

}
