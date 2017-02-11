package farm.bsg.pages;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.PageBootstrap;
import farm.bsg.PageTesting;

public class ChecksTest extends PageTesting{

    @Test
    public void Coverage() throws Exception {
        PageBootstrap bootstrap = go();
        String html = bootstrap.GET("/checks", params());
        Assert.assertNotNull(html);
    }
}
