package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class SubscriptionTest {
    @Test
    public void Coverage() {
        final Subscription subscription = new Subscription();
        Assert.assertNotNull(subscription.toJson());
    }

}
