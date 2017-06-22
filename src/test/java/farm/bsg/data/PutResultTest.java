package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

public class PutResultTest {

    @Test
    public void fieldFailure() {
        PutResult result = new PutResult();
        Assert.assertTrue(result.success());
        result.addFieldFailure("cake", "wtf");
        Assert.assertFalse(result.success());
        Assert.assertEquals("wtf", result.getErrors("cake").get(0));
    }

    @Test
    public void storageFailure() {
        PutResult result = new PutResult();
        Assert.assertTrue(result.success());
        result.setFailedStorage();
        Assert.assertFalse(result.success());
        Assert.assertTrue(result.wasStorageFailure());
    }

    @Test
    public void tooMuchData() {
        PutResult result = new PutResult();
        Assert.assertTrue(result.success());
        result.setTooMuchData();
        Assert.assertFalse(result.success());
        Assert.assertTrue(result.wasTooMuchData());
    }
}
