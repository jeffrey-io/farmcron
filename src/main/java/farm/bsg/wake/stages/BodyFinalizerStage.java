package farm.bsg.wake.stages;

import java.util.ArrayList;
import java.util.Collection;

import farm.bsg.wake.sources.BodyFinalizerSource;
import farm.bsg.wake.sources.BodyFinalizerSource.BodyMutator;
import farm.bsg.wake.sources.Source;

/**
 * This essentially allows a final stage to be done on the body of each source using a BodyMutator
 * 
 * @author jeffrey
 */
public class BodyFinalizerStage extends Stage {

    private final Stage       prior;
    private final BodyMutator mutator;

    public BodyFinalizerStage(final Stage prior, final BodyMutator mutator) {
        this.prior = prior;
        this.mutator = mutator;
    }

    @Override
    public Collection<Source> sources() {
        final ArrayList<Source> next = new ArrayList<Source>();
        for (final Source src : prior.sources()) {
            next.add(new BodyFinalizerSource(src, mutator));
        }
        return next;
    }

}
