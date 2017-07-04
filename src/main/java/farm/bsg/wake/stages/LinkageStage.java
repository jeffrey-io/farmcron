/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import farm.bsg.wake.sources.LinkageSource;
import farm.bsg.wake.sources.Source;

/**
 * This will cross all the pages together, and enable linking between final pages
 */
public class LinkageStage extends Stage {

    private final Stage stage;

    public LinkageStage(final Stage stage) {
        this.stage = stage;
    }

    @Override
    public Collection<Source> sources() {
        final ArrayList<Source> next = new ArrayList<>();
        final HashMap<String, Source> links = new HashMap<>();
        for (final Source src : this.stage.sources()) {
            final String name = src.get("name");
            if (name != null) {
                links.put(name, src);
            }
            // note, this is all lazily linked, so the links
            // should not be acted upon at this stage
            next.add(new LinkageSource(src, links));
        }
        return next;
    }

}
