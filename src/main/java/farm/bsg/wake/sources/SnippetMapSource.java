/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.sources;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * This enables one to take a source and then inject values either above the given source or under the given source (when it is not present).
 */
public class SnippetMapSource extends Source {
    private final Source              real;
    private final Map<String, String> snippetsHigher;
    private final Map<String, String> snippetsLower;

    public SnippetMapSource(final Source real, final Map<String, String> snippetsHigher, final Map<String, String> snippetsLower) {
        this.real = real;
        this.snippetsHigher = snippetsHigher;
        this.snippetsLower = snippetsLower;
    }

    @Override
    public String get(final String key) {
        String value = this.snippetsHigher.get(key);
        if (value != null) {
            return value;
        }
        value = this.real.get(key);
        if (value != null) {
            return value;
        }
        return this.snippetsLower.get(key);
    }

    @Override
    public void populateDomain(final Set<String> domain) {
        domain.addAll(this.snippetsHigher.keySet());
        this.real.populateDomain(domain);
        domain.addAll(this.snippetsLower.keySet());
    }

    @Override
    public void walkComplex(final BiConsumer<String, Object> injectComplex) {
        this.real.walkComplex(injectComplex);
    }
}
