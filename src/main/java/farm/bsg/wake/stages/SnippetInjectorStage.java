/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import farm.bsg.wake.sources.SnippetMapSource;
import farm.bsg.wake.sources.Source;
import farm.bsg.wake.sources.Source.SourceType;

/**
 * Filters out the snippets and then injects them into every non-snippet source TODO: document annotations
 */
public class SnippetInjectorStage extends Stage {
    private final Stage prior;

    public SnippetInjectorStage(final Stage prior) {
        this.prior = prior;
    }

    @Override
    public Collection<Source> sources() {
        final ArrayList<Source> preSnippetInjector = new ArrayList<>();
        final HashMap<String, String> snippets = new HashMap<>();
        for (final Source source : this.prior.sources()) {
            if (source.getType() == SourceType.Snippet) {
                snippets.put(source.get("name"), source.get("body"));
            } else {
                preSnippetInjector.add(source);
            }
        }
        final ArrayList<Source> next = new ArrayList<>();
        for (final Source source : preSnippetInjector) {
            next.add(new SnippetMapSource(source, Collections.emptyMap(), snippets));
        }
        return next;
    }
}
