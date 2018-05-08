package com.eegeo.mapapi.precaching;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;

public class PrecacheApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private final double m_maximumPrecacheRadius;
    private SparseArray<PrecacheOperation> m_operations = new SparseArray<>();

    @WorkerThread
    public PrecacheApi(
            INativeMessageRunner nativeRunner,
            IUiMessageRunner uiRunner,
            long jniEegeoMapApiPtr) {
        m_nativeRunner = nativeRunner;
        m_uiRunner = uiRunner;
        m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
        m_maximumPrecacheRadius = PrecacheApiJniCalls.getMaximumPrecacheRadius();
    }

    @UiThread
    public PrecacheOperation precache(LatLng center,
                                      double radius,
                                      OnPrecacheOperationCompletedListener callback) {
        return new PrecacheOperation(this, center, radius, callback);
    }

    @UiThread
    public double getMaximumPrecacheRadius() {
        return m_maximumPrecacheRadius;
    }


    @WorkerThread
    int beginPrecacheOperation(LatLng center, double radius) {
        return PrecacheApiJniCalls.beginPrecacheOperation(
                m_jniEegeoMapApiPtr, center.latitude, center.longitude, radius);
    }

    @WorkerThread
    void register(PrecacheOperation operation, int operationId) {
        m_operations.put(operationId, operation);
    }

    @WorkerThread
    void cancelPrecacheOperation(int operationId) {
        PrecacheApiJniCalls.cancelPrecacheOperation(m_jniEegeoMapApiPtr, operationId);
    }


    @WorkerThread
    public void notifyPrecacheOperationComplete(int precacheOperationId, final PrecacheOperationResult result) {
        final PrecacheOperation operation = m_operations.get(precacheOperationId);

        if (operation != null) {
            m_operations.remove(precacheOperationId);
            m_uiRunner.runOnUiThread(
                new Runnable() {
                    @UiThread
                    @Override
                    public void run() {
                        operation.returnResult(result);
                    }
                });
        }
    }

    @UiThread
    INativeMessageRunner getNativeRunner() { return m_nativeRunner; }

    @UiThread
    IUiMessageRunner getUiRunner() { return m_uiRunner; }
}
