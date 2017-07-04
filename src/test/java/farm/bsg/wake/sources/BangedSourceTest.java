package farm.bsg.wake.sources;

import java.io.Reader;

import org.junit.Test;

import farm.bsg.wake.TestingBase;

/**
 * Created by jeffrey on 4/9/14.
 */
public class BangedSourceTest extends TestingBase {

    @Test
    public void testBangParsing() throws Exception {
        final Reader reader = readerize("#!key=value\nbody");
        final BangedSource source = new BangedSource("filename.txt", reader);
        assertEvaluate("key", source, "value");
        assertEvaluate("body", source, "body\n");
        assertEvaluate("name", source, "filename");
        assertEvaluate("url", source, "filename.html");
        assertItemization(source, "key", "body", "name", "url");
    }

    @Test
    public void testOverrideDefaultNameAndUrlPossible() throws Exception {
        final Reader reader = readerize("#!key=value\nbody\n#!name=fooness\n#!url=some-faux-folder");
        final BangedSource source = new BangedSource("filename.txt", reader);
        assertEvaluate("key", source, "value");
        assertEvaluate("body", source, "body\n");
        assertEvaluate("name", source, "fooness");
        assertEvaluate("url", source, "some-faux-folder");
        assertItemization(source, "key", "body", "name", "url");
    }
}
