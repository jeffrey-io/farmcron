package farm.bsg.wake.stages;

import farm.bsg.wake.TestingBase;

import org.junit.Test;

import farm.bsg.wake.sources.HashMapSource;
import farm.bsg.wake.sources.Source;

/**
 * Created by jeffrey on 4/9/14.
 */
public class SnippetInjectorStageTest extends TestingBase {

   @Test
   public void testSnippetMerged() {
      HashMapSource snippet = createVerySimpleSource();
      snippet.put("type", "snippet");
      snippet.put("name", "meme");
      snippet.put("body", "cowboy");

      HashMapSource raw = createVerySimpleSource();
      raw.put("me", "me");

      Stage stages = stageOf(snippet, raw);
      SnippetInjectorStage snippets = new SnippetInjectorStage(stages);

      Source merged = getExactlyOne(snippets);
      assertEvaluate("me", merged, "me");
      assertEvaluate("meme", merged, "cowboy");
   }
}
