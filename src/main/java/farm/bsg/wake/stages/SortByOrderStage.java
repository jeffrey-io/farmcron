/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import farm.bsg.wake.sources.Source;

/**
 * Sorts the sources by their order property
 */
public class SortByOrderStage extends Stage {
    private final Stage priorStage;

    public SortByOrderStage(final Stage priorStage) {
        this.priorStage = priorStage;
    }

    @Override
    public Collection<Source> sources() {
        final ArrayList<Source> sources = new ArrayList<>();
        sources.addAll(this.priorStage.sources());
        Collections.sort(sources, Comparator.comparingLong((item) -> item.order()));
        return sources;
    }
}
