package com.eegeo.mapapi.util;

/**
 * Interface for objects which consume the result of an asynchronous operation.
 * @param <T> Type of the result computed asynchronously.
 */
public interface Ready<T> {
    /**
     * Function which will be called when the result is available.
     * @param t The result.
     */
    void ready(T t);
}
