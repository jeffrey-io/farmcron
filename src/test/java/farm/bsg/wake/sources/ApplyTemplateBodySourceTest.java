package farm.bsg.wake.sources;

import org.junit.Test;

import farm.bsg.wake.TestingBase;

/**
 * Created by jeffrey on 4/8/14.
 */
public class ApplyTemplateBodySourceTest extends TestingBase {

    @Test
    public void testDetectInfiniteLoop() {
        final HashMapSource data = createVerySimpleSource();
        data.put("place", "data:{{{place}}}");
        final HashMapSource template = createVerySimpleSource();
        template.put("body", "place={{{place}}}");
        final ApplyTemplateBodySource apply = new ApplyTemplateBodySource(data, template);
        try {
            apply.get("body");
            fail("should be exhausted");
        } catch (final SourceException se) {

        }
    }

    @Test
    public void testTemplateApplicationResults() {
        final HashMapSource data = createVerySimpleSource();
        data.put("something", "XYZ");
        data.put("other", "AC/DC");
        final HashMapSource template = createVerySimpleSource();
        template.put("body", "[{{something}}]=what? {{other}}");
        final ApplyTemplateBodySource apply = new ApplyTemplateBodySource(data, template);
        assertBodyEvaluate(apply, "[XYZ]=what? AC/DC");
        assertItemization(apply, "body", "something", "other");
    }

    @Test
    public void testTemplateDefinesDefault() {
        final HashMapSource data = createVerySimpleSource();
        data.put("place", "ABC");
        data.put("something", "XYZ");
        data.put("something", "{{place}}");
        data.put("other", "AC/DC");
        final HashMapSource template = createVerySimpleSource();
        template.put("body", "[{{something}}]=what? {{other}}");
        template.put("url", "foo");
        template.put("place", "not here");
        final ApplyTemplateBodySource apply = new ApplyTemplateBodySource(data, template);
        assertBodyEvaluate(apply, "[ABC]=what? AC/DC");
        assertItemization(apply, "body", "something", "other", "place");
        assertEvaluate("url", apply, "foo");
        assertEvaluate("place", apply, "ABC");
    }

    @Test
    public void testTemplatingIsVeryLazy() {
        final HashMapSource data = createVerySimpleSource();
        final HashMapSource template = createVerySimpleSource();
        final ApplyTemplateBodySource apply = new ApplyTemplateBodySource(data, template);
        template.put("body", "[{{something}}]=what? {{other}}");
        assertBodyEvaluate(apply, "[]=what? ");
        data.put("place", "ABC");
        data.put("something", "XYZ");
        data.put("something", "{{place}}");
        data.put("other", "AC/DC");
        template.put("body", "[{{something}}]=what? {{other}}");
        assertBodyEvaluate(apply, "[ABC]=what? AC/DC");
        assertItemization(apply, "body", "something", "other", "place");
    }

    @Test
    public void testTemplatingTailRecursionApplication() {
        final HashMapSource data = createVerySimpleSource();
        data.put("place", "ABC");
        data.put("something", "XYZ");
        data.put("something", "{{place}}");
        data.put("other", "AC/DC");
        final HashMapSource template = createVerySimpleSource();
        template.put("body", "[{{something}}]=what? {{other}}");
        final ApplyTemplateBodySource apply = new ApplyTemplateBodySource(data, template);
        assertBodyEvaluate(apply, "[ABC]=what? AC/DC");
        assertItemization(apply, "body", "something", "other", "place");
    }

    @Test
    public void testTemplatingWithChildBody() {
        final HashMapSource data = createVerySimpleSource();
        data.put("body", "<a href=\"http://FOO\">");
        final HashMapSource template = createVerySimpleSource();
        template.put("body", "{{{body}}}");
        final ApplyTemplateBodySource apply = new ApplyTemplateBodySource(data, template);
        assertBodyEvaluate(apply, "<a href=\"http://FOO\">");
        assertItemization(apply, "body");
    }

}
