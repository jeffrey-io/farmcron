package farm.bsg.wake.sources;

import java.util.HashMap;

import org.junit.Test;

import farm.bsg.wake.TestingBase;

/**
 * Created by jeffrey on 4/9/14.
 */
public class SnippetMapSourceTest extends TestingBase {

    @Test
    public void testInjectionAndPrecedender() {
        final HashMapSource real = createVerySimpleSource();
        real.put("keep", "me");
        real.put("hide", "real");
        final HashMap<String, String> over = new HashMap<>();
        final HashMap<String, String> under = new HashMap<>();
        over.put("hide", "over");
        under.put("hide", "under");
        over.put("over", "1");
        under.put("under", "2");
        final SnippetMapSource withSnippets = new SnippetMapSource(real, over, under);
        assertEvaluate("keep", withSnippets, "me");
        assertEvaluate("hide", withSnippets, "over");
        assertEvaluate("over", withSnippets, "1");
        assertEvaluate("under", withSnippets, "2");

        assertItemization(withSnippets, "keep", "hide", "over", "under");
    }
}
