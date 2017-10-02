package com.eegeo.mapapi.picking;


import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.Promise;

/**
 * @eegeo.internal
 */
public class PickingApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    /**
     * @eegeo.internal
     */
    public PickingApi(INativeMessageRunner nativeRunner,
                        IUiMessageRunner uiRunner,
                        long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    /**
     * @eegeo.internal
     */
    @UiThread
    public Promise<PickResult> pickFeatureAtScreenPoint(@NonNull final Point point) {
        final Promise<PickResult> p = new Promise<>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final PickResult pickResult = nativePickFeatureAtScreenPoint(m_jniEegeoMapApiPtr, point.x, point.y);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(pickResult);
                    }
                });
            }
        });
        return p;
    }

    /**
     * @eegeo.internal
     */
    @UiThread
    public Promise<PickResult> pickFeatureAtLatLng(@NonNull final LatLng latLng) {
        final Promise<PickResult> p = new Promise<>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final PickResult pickResult = nativePickFeatureAtLatLng(m_jniEegeoMapApiPtr, latLng.latitude, latLng.longitude);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(pickResult);
                    }
                });
            }
        });
        return p;
    }


    @WorkerThread
    private native PickResult nativePickFeatureAtScreenPoint(long jniEegeoMapApiPtr, double x, double y);

    @WorkerThread
    private native PickResult nativePickFeatureAtLatLng(long jniEegeoMapApiPtr, double lat, double lng);

}
