package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class TaskFactoryTest {
    @Test
    public void Readyness() {
        final TaskFactory factory = new TaskFactory();
        final long now = 1498099238370L;
        for (int k = 0; k < 100; k++) {
            Assert.assertTrue(factory.ready(now + k * 24 * 60 * 60 * 1000));
        }
        factory.set("month_filter", "l");
        Assert.assertFalse(factory.ready(now));
        Assert.assertFalse(factory.ready(now + 5 * 24 * 60 * 60 * 1000));
        Assert.assertTrue(factory.ready(now + 10 * 24 * 60 * 60 * 1000));
        factory.set("day_filter", "s");
        Assert.assertFalse(factory.ready(now + 10 * 24 * 60 * 60 * 1000));
        Assert.assertTrue(factory.ready(now + 11 * 24 * 60 * 60 * 1000));
        Assert.assertFalse(factory.ready(now + 12 * 24 * 60 * 60 * 1000));
        Assert.assertFalse(factory.ready(now + 13 * 24 * 60 * 60 * 1000));
        Assert.assertFalse(factory.ready(now + 14 * 24 * 60 * 60 * 1000));
        Assert.assertFalse(factory.ready(now + 15 * 24 * 60 * 60 * 1000));
        Assert.assertFalse(factory.ready(now + 16 * 24 * 60 * 60 * 1000));
        Assert.assertFalse(factory.ready(now + 17 * 24 * 60 * 60 * 1000));
        Assert.assertTrue(factory.ready(now + 18 * 24 * 60 * 60 * 1000));
    }
}
