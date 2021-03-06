/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.sources;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A source is basically a very special key value pair map where we lazily enable redefinition of the map.
 * <p>
 * The idea is that you can ask a source for any key, and it will return the value if it exists (null otherwise).
 * <p>
 * The kicker (and the real novel idea here) is that the the source has no standard domain/keySet. Instead, you ask for the domain and it will tell you what keys it provides. The nice thing about this is a wrapping layer may intercept that key and use it.
 */
public abstract class Source implements Comparable<Source> {

    public static enum SourceType {
        Snippet, Template, Page
    }

    @Override
    public int compareTo(final Source o) {
        return Long.compare(this.order(), o.order());
    }

    /**
     * get the value from the source
     *
     * @param key
     *            the key (found in the set that is populated via populateDomain; "body" is a fairly common key)
     * @return
     */
    public abstract String get(String key);

    public SourceType getType() {
        if ("snippet".equalsIgnoreCase(get("type"))) {
            return SourceType.Snippet;
        }
        if (null != get("template-name")) {
            return SourceType.Template;
        }
        return SourceType.Page;
    }

    /**
     * Get the order from the object; if you sort this
     *
     * @return
     */
    public long order() {
        final String ord = get("order");
        if (ord == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(ord);
    }

    /**
     * Populate the given set with all the possible strings that can be acquired from the get() method
     *
     * @param domain
     */
    public abstract void populateDomain(Set<String> domain);

    /**
     * Test whether or not the key has a boolean value like yes or true
     */
    public boolean testBoolean(final String key) {
        String value = get(key);
        if (value == null) {
            return false;
        }
        value = value.toLowerCase();
        if (value.length() == 0) {
            return false;
        }
        if ("1".equals(value)) {
            return true;
        }
        return value.startsWith("t") || value.startsWith("y");
    }

    /**
     * A source may define things too complicated to return as a string, so instead, a source may provide things in the form of an Object that the top level can decide what to do with
     *
     * @param injectComplex
     */
    public abstract void walkComplex(BiConsumer<String, Object> injectComplex);
}
