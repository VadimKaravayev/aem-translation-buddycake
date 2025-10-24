package com.aem.translation.connector.buddycake.core.util.fp;

@FunctionalInterface
public interface CheckedRunnable<E extends Exception> {
    void run() throws E;
}
