package farm.bsg.wake.sources;

import org.junit.Test;

import farm.bsg.wake.TestingBase;

/**
 * Created by jeffrey on 4/9/14.
 */
public class TagsFilteredSourceTest extends TestingBase {

    @Test
    public void testTagDetection() {
        final HashMapSource raw = createVerySimpleSource();
        raw.put("body", "&&a&&");
        final TagsFilteredSource tagged = new TagsFilteredSource(raw);
        assertBodyEvaluate(tagged, "<em class=\"tag\">a</em>");
    }

    @Test
    public void testTagDetectionAdj() {
        final HashMapSource raw = createVerySimpleSource();
        raw.put("body", "&&a&&&&b&&");
        final TagsFilteredSource tagged = new TagsFilteredSource(raw);
        assertBodyEvaluate(tagged, "<em class=\"tag\">a</em><em class=\"tag\">b</em>");
    }

    @Test
    public void testTagDetectionAdjAndMixed() {
        final HashMapSource raw = createVerySimpleSource();
        raw.put("body", "X&&a&&Y&&b&&Z");
        final TagsFilteredSource tagged = new TagsFilteredSource(raw);
        assertBodyEvaluate(tagged, "X<em class=\"tag\">a</em>Y<em class=\"tag\">b</em>Z");
    }
}
