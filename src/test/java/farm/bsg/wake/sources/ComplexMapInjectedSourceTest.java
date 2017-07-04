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
        HashMapSource dataRaw = createVerySimpleSource();
        dataRaw.put("body", "howdy");
        HashMap<String, Object> map = new HashMap<>();
        map.put("foo", "kicker");
        ComplexMapInjectedSource data = new ComplexMapInjectedSource(dataRaw, "map", map);
        HashMapSource template = createVerySimpleSource();
        template.put("body", "{{body}}{{#map}}{{foo}}{{/map}}{{body}}");
        ApplyTemplateBodySource finalSource = new ApplyTemplateBodySource(data, template);
        assertBodyEvaluate(finalSource, "howdykickerhowdy");
    }
}
