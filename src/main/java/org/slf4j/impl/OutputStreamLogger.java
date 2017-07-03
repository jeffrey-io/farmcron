package org.slf4j.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

import farm.bsg.ops.Logs;

public class OutputStreamLogger extends OutputStream {
    private final String prefix;
    private final Logger log;

    public OutputStreamLogger(String prefix) {
        this.prefix = prefix;
        this.log = Logs.of(OutputStreamLogger.class, prefix);
    }

    @Override
    public void write(int b) throws IOException {
        if (b >= 0) {
            write(new byte[] { (byte) b }, 0, 1);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        log.debug(new String(b, off, len));
    }

}
