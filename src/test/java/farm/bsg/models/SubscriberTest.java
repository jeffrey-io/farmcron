package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class SubscriberTest {
    @Test
    public void Coverage() {
        Subscriber subcriber = new Subscriber();
        Assert.assertNotNull(subcriber.toJson());
    }

}
