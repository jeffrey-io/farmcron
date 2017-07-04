package farm.bsg.wake.stages;

import java.util.Collection;

import org.junit.Test;

import farm.bsg.wake.TestingBase;
import farm.bsg.wake.sources.HashMapSource;
import farm.bsg.wake.sources.Source;

/**
 * Created by jeffrey on 4/9/14.
 */
public class SortByOrderStageTest extends TestingBase {

    private Source orderedSource(final int order) {
        final HashMapSource source = createVerySimpleSource();
        source.put("order", "" + order);
        return source;
    }

    @Test
    public void testOrdering() {
        final Stage stage = stageOf(orderedSource(6), orderedSource(1), orderedSource(4), orderedSource(2), orderedSource(0), orderedSource(3), orderedSource(5));
        final Collection<Source> sorted = new SortByOrderStage(stage).sources();
        final StringBuilder orders = new StringBuilder();
        for (final Source source : sorted) {
            orders.append("[" + source.order() + "]");
        }
        assertEquals("[0][1][2][3][4][5][6]", orders.toString());
    }
}
