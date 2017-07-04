package farm.bsg.wake;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Ignore;

import farm.bsg.wake.sources.HashMapSource;
import farm.bsg.wake.sources.Source;
import farm.bsg.wake.stages.SetStage;
import farm.bsg.wake.stages.Stage;

/**
 * Created by jeffrey on 4/8/14.
 */
@Ignore
public class TestingBase {

    private static String lastTest = "?";

    protected void assertBodyEvaluate(final Source source, final String expected) {
        assertEvaluate("body", source, expected);
    }

    protected void assertEquals(final String expected, final String computed) {
        logCheck("'" + computed + "'='" + expected + "'");
        if (expected.equals(computed)) {
            return;
        }
        throw new AssertionError("expected:'" + expected + "', but got '" + computed + "'");
    }

    protected void assertEvaluate(final String key, final Source source, final String expected) {
        final String computed = source.get(key);
        logCheck(key + ":'" + computed + "'='" + expected + "'");
        if (expected.equals(computed)) {
            return;
        }
        throw new AssertionError("expected:\n'" + expected + "'\n, but got \n'" + computed + "'");
    }

    protected void assertItemization(final Source source, final String... keys) {
        final HashSet<String> itemizedKeys = new HashSet<>();
        source.populateDomain(itemizedKeys);
        for (final String keyToCheck : keys) {
            logCheck(itemizedKeys.toString() + " contains '" + keyToCheck + "'");
            if (!itemizedKeys.contains(keyToCheck)) {
                throw new AssertionError("itemization lacked '" + keyToCheck + "'");
            }
        }
    }

    public HashMapSource createVerySimpleSource() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("title", "Z' Title");
        map.put("body", "body");
        return new HashMapSource(map);
    }

    protected void fail(final String why) {
        throw new AssertionError("we expected to fail:" + why);
    }

    protected Source getExactlyOne(final Stage stages) {
        final Collection<Source> sources = stages.sources();
        logCheck("one size check:" + sources.size() + ":" + stages.getClass().getName());
        if (1 != sources.size()) {
            throw new AssertionError("size was not 1");
        }
        return sources.iterator().next();
    }

    protected void logCheck(final String msg) {
        String test = "?";
        try {
            throw new NullPointerException();
        } catch (final NullPointerException npe) {
            for (final StackTraceElement ste : npe.getStackTrace()) {
                if (ste.getMethodName().startsWith("test")) {
                    test = ste.getMethodName().substring(4);
                }
            }
        }
        if (!lastTest.equals(test)) {
            System.out.println();
            System.out.println("begin[" + test + "]");
            lastTest = test;
        }
        System.out.println("check[" + test + "]:" + msg);
    }

    protected Reader readerize(final String value) {
        return new InputStreamReader(new ByteArrayInputStream(value.getBytes()));
    }

    protected Stage stageOf(final Source... sources) {
        final HashSet<Source> set = new HashSet<>();
        for (final Source source : sources) {
            set.add(source);
        }
        return new SetStage(set);
    }

}
