package com.aem.translation.connector.buddycake.core.util.fp;

@FunctionalInterface
public interface CheckedSupplier<T, E extends Exception> {
    T get() throws E;
}
