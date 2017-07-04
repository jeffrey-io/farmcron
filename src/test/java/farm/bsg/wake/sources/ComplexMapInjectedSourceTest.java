package farm.bsg.wake.sources;

import java.util.HashMap;

import org.junit.Test;

import farm.bsg.wake.TestingBase;

/**
 * Created by jeffrey on 4/9/14.
 */
public class ComplexMapInjectedSourceTest extends TestingBase {

    @Test
    public void testComplexMapInjectionWithTemplating() {
        final HashMapSource dataRaw = createVerySimpleSource();
        dataRaw.put("body", "howdy");
        final HashMap<String, Object> map = new HashMap<>();
        map.put("foo", "kicker");
        final ComplexMapInjectedSource data = new ComplexMapInjectedSource(dataRaw, "map", map);
        final HashMapSource template = createVerySimpleSource();
        template.put("body", "{{body}}{{#map}}{{foo}}{{/map}}{{body}}");
        final ApplyTemplateBodySource finalSource = new ApplyTemplateBodySource(data, template);
        assertBodyEvaluate(finalSource, "howdykickerhowdy");
    }
}
