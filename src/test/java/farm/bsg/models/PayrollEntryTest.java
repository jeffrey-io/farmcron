package farm.bsg.models;

import org.junit.Assert;
import org.junit.Test;

public class PayrollEntryTest {
    @Test
    public void Coverage() {
        PayrollEntry payroll = new PayrollEntry();
        Assert.assertNotNull(payroll.toJson());
    }

}
