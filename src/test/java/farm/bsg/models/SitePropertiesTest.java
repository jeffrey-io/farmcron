package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class SitePropertiesTest {
    @Test
    public void Coverage() {
        final SiteProperties site = new SiteProperties();
        Assert.assertNotNull(site.toJson());
    }

}
