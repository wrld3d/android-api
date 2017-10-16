package com.eegeo.mapapi.services;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;


public class PoiService {

    private INativeMessageRunner m_nativeRunner;
    private long m_jniEegeoMapApiPtr;


    public PoiService(INativeMessageRunner nativeRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    public void search(final String query, final LatLng latLng) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                nativeSearch(
                        m_jniEegeoMapApiPtr,
                        query,
                        latLng.latitude,
                        latLng.longitude,
                        true, 1000.0,
                        true, 20,
                        true, 0.0,
                        true, "westport_house",
                        true, 2,
                        true, 15);
            }
        });
    }


    private static native void nativeSearch(
            long jniEegeoMapApiPtr,
            String query,
            double latitude,
            double longitude,
            boolean useRadius, double radius,
            boolean useNumber, int number,
            boolean useMinScore, double minScore,
            boolean useIndoorId, String indoorId,
            boolean useFloor, int floor,
            boolean useFloorDropoff, int floorDropoff);
}

