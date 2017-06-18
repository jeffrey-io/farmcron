package farm.bsg.wake.stages;

import farm.bsg.wake.TestingBase;

import org.junit.Test;

import farm.bsg.wake.sources.HashMapSource;
import farm.bsg.wake.sources.Source;

/**
 * Created by jeffrey on 4/9/14.
 */
public class TemplateCrossStageTest extends TestingBase{

   @Test
   public void testTemplating() {
      HashMapSource template = createVerySimpleSource();
      HashMapSource data = createVerySimpleSource();
      template.put("template-name", "t");
      data.put("use-template", "t");
      data.put("x", "123");
      data.put("y", "ninja");
      template.put("body", "the body is {{x}} and {{y}}");
      Stage flat = stageOf(data, template);
      TemplateCrossStage cross = new TemplateCrossStage(flat);
      Source composite = getExactlyOne(cross);
      assertBodyEvaluate(composite, "the body is 123 and ninja");
   }
}
