/*
 * Copyright 2014 Jeffrey M. Barber; see LICENSE for more details
 */
package farm.bsg.wake.stages;

import java.util.Collection;

import farm.bsg.wake.sources.Source;

/**
 * Defines a stage of the site generation
 */
public abstract class Stage {

    public abstract Collection<Source> sources();
}
