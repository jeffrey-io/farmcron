package farm.bsg.data;

import java.io.File;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.TestWorld;

public class DiskStorageLoggerTest {

    @Test
    public void WriteAndPump() throws Exception {
        final File temp = new File("/tmp/disk.logger." + UUID.randomUUID().toString());
        temp.mkdir();
        try {
            final DiskStorageLogger disk = new DiskStorageLogger(temp);
            disk.put("a", TestWorld.value_start().with("hey", "you").done());
            disk.put("b", TestWorld.value_start().with("what", "said").done());
            final InMemoryStorage memory = new InMemoryStorage();
            disk.pump(memory);
            Assert.assertEquals("{\"hey\":\"you\"}", memory.get("a").toString());
            Assert.assertEquals("{\"what\":\"said\"}", memory.get("b").toString());
        } finally {
            for (final File file : temp.listFiles()) {
                file.delete();
            }
            temp.delete();
        }
    }
}
