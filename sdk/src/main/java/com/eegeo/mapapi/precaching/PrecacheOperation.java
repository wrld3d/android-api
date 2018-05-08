package com.eegeo.mapapi.precaching;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

/**
 * A handle to an ongoing precache operation.
 */
public class PrecacheOperation extends NativeApiObject {
    private OnPrecacheOperationCompletedListener m_callback;
    private PrecacheApi m_precacheApi;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.precache to start a precache
     * operation
     *
     * @eegeo.internal
     */
    @UiThread
    PrecacheOperation(
            @NonNull final PrecacheApi precacheApi,
            @NonNull final LatLng center,
            final double radius,
            final OnPrecacheOperationCompletedListener callback) {
        super(precacheApi.getNativeRunner(), precacheApi.getUiRunner(),
                new Callable<Integer>() {
                    @Override
                    public Integer call() {
                        return precacheApi.beginPrecacheOperation(
                                center, radius);
                    }
                });

        m_callback = callback;
        m_precacheApi = precacheApi;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_precacheApi.register(PrecacheOperation.this, getNativeHandle());
            }
        });
    }

    /**
     * Cancels this precache operation if it has not yet been completed.
     */
    @UiThread
    public void cancel() {
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_precacheApi.cancelPrecacheOperation(getNativeHandle());
            }
        });
    }

    @UiThread
    void returnResult(PrecacheOperationResult result) {
        if (m_callback != null) {
            m_callback.onPrecacheOperationCompleted(result);
        }
    }
}
