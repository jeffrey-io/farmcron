package farm.bsg.wake.stages;

import org.junit.Test;

import farm.bsg.wake.TestingBase;
import farm.bsg.wake.sources.HashMapSource;
import farm.bsg.wake.sources.Source;

/**
 * Created by jeffrey on 4/9/14.
 */
public class SnippetInjectorStageTest extends TestingBase {

    @Test
    public void testSnippetMerged() {
        final HashMapSource snippet = createVerySimpleSource();
        snippet.put("type", "snippet");
        snippet.put("name", "meme");
        snippet.put("body", "cowboy");

        final HashMapSource raw = createVerySimpleSource();
        raw.put("me", "me");

        final Stage stages = stageOf(snippet, raw);
        final SnippetInjectorStage snippets = new SnippetInjectorStage(stages);

        final Source merged = getExactlyOne(snippets);
        assertEvaluate("me", merged, "me");
        assertEvaluate("meme", merged, "cowboy");
    }
}
