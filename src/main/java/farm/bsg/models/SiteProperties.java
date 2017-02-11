package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class SiteProperties extends RawObject {
    public SiteProperties() {
        super("site/", //
                Field.STRING("domain"), // i.e. bsg.farm (will be used once this data is global)
                Field.STRING("product_name"), //

                Field.STRING("fb_page_token"), //

                Field.STRING("description"), //
                Field.STRING("equipment") // what equipment is available
        );
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Data: SiteProperties");
    }
    
    @Override
    protected void invalidateCache() {
    }

}
