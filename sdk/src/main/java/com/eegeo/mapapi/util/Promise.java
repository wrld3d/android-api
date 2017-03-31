package com.eegeo.mapapi.util;

/**
 * Promise pattern for handling asynchronous operations.
 *
 * @param <T> Type of the value to be computed asynchronously
 */
public class Promise<T> {
    private Ready<T> m_func = null;

    /**
     * Called by the operation which has computed the value requested.
     * @param t
     */
    public void ready(T t) {
        if (m_func != null) {
            m_func.ready(t);
        }
    }

    /**
     * Sets an object which consumes the result of the asyncronous operation.
     * @param f Object which consumes the result via its ready method.
     */
    public void then(Ready<T> f) {
        m_func = f;
    }
}