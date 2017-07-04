package farm.bsg.pages;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.PageBootstrap;
import farm.bsg.PageTesting;

public class DashboardTest extends PageTesting {

    @Test
    public void Coverage() throws Exception {
        final PageBootstrap bootstrap = go();
        final String html = bootstrap.GET("/dashboard", params());
        Assert.assertNotNull(html);
    }
}
