package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class PersonTest {
    @Test
    public void Coverage() {
        final Person person = new Person();
        Assert.assertNotNull(person.toJson());
    }

}
