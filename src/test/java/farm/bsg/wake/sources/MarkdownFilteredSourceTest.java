package farm.bsg.wake.sources;

import org.junit.Test;

import farm.bsg.wake.TestingBase;

/**
 * Created by jeffrey on 4/9/14.
 */
public class MarkdownFilteredSourceTest extends TestingBase {

    @Test
    public void testHtmlProduction() {
        final String body = "ninja\n# header #\n ## header2 ##\n* a\n* b";
        final HashMapSource data = createVerySimpleSource();
        data.put("body", body);
        final MarkdownFilteredSource filtered = new MarkdownFilteredSource(data, "body");
        final String expected = "<p>ninja</p>\n" + "<h1>header</h1>\n" + "<h2>header2</h2>\n" + "<ul>\n" + "<li>a</li>\n" + "<li>b</li>\n" + "</ul>\n";
        assertBodyEvaluate(filtered, expected);
        assertItemization(filtered, "body");
    }

    @Test
    public void testLinks() {
        final String body = "[J](http://jeffrey.io)";
        final HashMapSource data = createVerySimpleSource();
        data.put("body", body);
        final MarkdownFilteredSource filtered = new MarkdownFilteredSource(data, "body");
        final String expected = "<p><a href=\"http://jeffrey.io\">J</a></p>\n";
        assertBodyEvaluate(filtered, expected);
        assertItemization(filtered, "body");
    }

    @Test
    public void testSkipNonFilteredKey() {
        final String body = "ninja\n# header #\n ## header2 ##\n* a\n* b";
        final HashMapSource data = createVerySimpleSource();
        data.put("body", body);
        data.put("not", body);
        final MarkdownFilteredSource filtered = new MarkdownFilteredSource(data, "body");
        assertEvaluate("not", filtered, body);
        assertItemization(filtered, "body", "not");
    }
}
