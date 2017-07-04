package farm.bsg.wake.stages;

import org.junit.Test;

import farm.bsg.wake.TestingBase;
import farm.bsg.wake.sources.HashMapSource;
import farm.bsg.wake.sources.Source;

public class BodyFinalizerStageTest extends TestingBase {

    @Test
    public void testMutator() {
        final HashMapSource snippet = createVerySimpleSource();
        snippet.put("body", "cowboy");
        snippet.put("x", "xyz");
        final BodyFinalizerStage fstage = new BodyFinalizerStage(stageOf(snippet), (body) -> {
            return "|" + body + "|";
        });
        final Source done = getExactlyOne(fstage);
        assertEvaluate("body", done, "|cowboy|");
        assertEvaluate("x", done, "xyz");
    }
}
