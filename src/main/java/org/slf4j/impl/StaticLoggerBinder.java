package org.slf4j.impl;

import org.slf4j.ILoggerFactory;

import farm.bsg.ops.LogDatabase;

public class StaticLoggerBinder {
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticLoggerBinder singleton
     */
    public static final StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * Declare the version of the SLF4J API this implementation is compiled against. The value of this field is modified with each major release.
     */
    // to avoid constant folding by the compiler, this field must *not* be final
    public static String REQUESTED_API_VERSION = "1.6.99"; // !final

    public final LogDatabase   database;

    public StaticLoggerBinder() {
        this.database = new LogDatabase();
    }

    public ILoggerFactory getLoggerFactory() {
        return database;
    }

    public String getLoggerFactoryClassStr() {
        return StaticLoggerBinder.class.getName() + ":" + database.getClass().getName();
    }
}
