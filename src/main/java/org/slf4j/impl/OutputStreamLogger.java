package org.slf4j.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

import farm.bsg.ops.Logs;

public class OutputStreamLogger extends OutputStream {
    private final Logger log;

    public OutputStreamLogger(final String prefix) {
        this.log = Logs.of(OutputStreamLogger.class, prefix);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.log.debug(new String(b, off, len));
    }

    @Override
    public void write(final int b) throws IOException {
        if (b >= 0) {
            write(new byte[] { (byte) b }, 0, 1);
        }
    }

}
