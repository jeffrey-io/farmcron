/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.sources;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * This enables one to inject complex maps (i.e. topologies and other objects) into the source for during the 'walkComplex' phase of rendering
 */
public class ComplexMapInjectedSource extends Source {
    private final Source source;
    private final String mapKey;
    private final Object mapValue;

    public ComplexMapInjectedSource(final Source source, final String mapKey, final Object mapValue) {
        this.source = source;
        this.mapKey = mapKey;
        this.mapValue = mapValue;
    }

    @Override
    public String get(final String key) {
        return this.source.get(key);
    }

    @Override
    public void populateDomain(final Set<String> domain) {
        this.source.populateDomain(domain);
    }

    @Override
    public void walkComplex(final BiConsumer<String, Object> injectComplex) {
        this.source.walkComplex(injectComplex);
        injectComplex.accept(this.mapKey, this.mapValue);
    }
}
