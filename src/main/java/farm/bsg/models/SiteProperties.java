package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class SiteProperties extends RawObject {
    
    public static final ObjectSchema SCHEMA = ObjectSchema.singleton("site/", //
            Field.STRING("domain"), // i.e. bsg.farm (will be used once this data is global)
            Field.STRING("product_name").withDefault("Demo Site"), //

            Field.STRING("fb_page_token"), //
            Field.NUMBER("product_imaging_thumbprint_size").withDefault(120),
            Field.NUMBER("product_imaging_normal_size").withDefault(400),
            Field.STRING("description")
    );
            
    public SiteProperties() {
        super(SCHEMA);
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: SiteProperties");
    }
    
    @Override
    protected void invalidateCache() {
    }

}
