package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class EventTest {
    @Test
    public void Coverage() {
        Event event = new Event();
        Assert.assertNotNull(event.toJson());
    }

}
