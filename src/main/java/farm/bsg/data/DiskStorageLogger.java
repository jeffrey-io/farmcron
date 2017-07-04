package farm.bsg.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import farm.bsg.data.contracts.KeyValueStoragePut;
import farm.bsg.data.contracts.PersistenceLogger;

/**
 * Log writes to disk, provide the ability to read all results from disk
 *
 * @author jeffrey
 */
public class DiskStorageLogger implements PersistenceLogger {
    /**
     * @return the normal version of the name
     */
    public static String decodeName(final String name) {
        try {
            return new String(Hex.decodeHex(name.toCharArray()));
        } catch (final DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return the encoded name
     */
    public static String encodeName(final String name) {
        return Hex.encodeHexString(name.getBytes());
    }

    private final File path;

    /**
     * @param path
     *            where to read/write data
     */
    public DiskStorageLogger(final File path) {
        this.path = path;
    }

    /**
     * read all data from disk
     *
     * @param storage;
     *            where ti put the data after reading it all
     * @throws IOException;
     *             we failed to read data
     */
    @Override
    public void pump(final KeyValueStoragePut storage) throws Exception {
        final File[] files = this.path.listFiles();
        if (files == null) {
            return;
        }
        for (final File file : files) {
            final Value value = new Value(new String(Files.readAllBytes(file.toPath())));
            final String name = decodeName(file.getName());
            storage.put(name, value);
        }
    }

    /**
     * write the data to the disk
     */
    @Override
    public boolean put(final String key, final Value newValue) {
        final File file = new File(this.path, encodeName(key));
        try {
            if (newValue == null) {
                if (file.exists()) {
                    file.delete();
                }
                return true;
            }
            Files.write(file.toPath(), newValue.getBytes());
            return true;
        } catch (final IOException e) {
            return false;
        }
    }
}
