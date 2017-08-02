package com.eegeo.mapapi.bluesphere;


import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;

public class BlueSphereApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;


    public BlueSphereApi(INativeMessageRunner nativeRunner,
                     IUiMessageRunner uiRunner,
                     long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    public INativeMessageRunner getNativeRunner() {
        return m_nativeRunner;
    }

    @UiThread
    public IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    @WorkerThread
    public void setCoordinate(BlueSphere.AllowHandleAccess allowHandleAccess, LatLng position) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BlueSphere");


        nativeSetCoordinate(
                m_jniEegeoMapApiPtr,
                position.latitude,
                position.longitude);
    }

    @WorkerThread
    public void setElevation(BlueSphere.AllowHandleAccess allowHandleAccess, double elevation) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BlueSphere");


        nativeSetElevation(
                m_jniEegeoMapApiPtr,
                elevation);
    }

    @WorkerThread
    public void setIndoorMap(BlueSphere.AllowHandleAccess allowHandleAccess, String indoorMap, int floorId) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BlueSphere");


        nativeSetIndoorMap(
                m_jniEegeoMapApiPtr,
                indoorMap,
                floorId);
    }

    @WorkerThread
    public void setEnabled(BlueSphere.AllowHandleAccess allowHandleAccess, boolean enabled) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BlueSphere");


        nativeSetEnabled(
                m_jniEegeoMapApiPtr,
                enabled);
    }

    @WorkerThread
    public void setDirection(BlueSphere.AllowHandleAccess allowHandleAccess, double headingDegreesFromNorth) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BlueSphere");


        nativeSetDirection(
                m_jniEegeoMapApiPtr,
                headingDegreesFromNorth);
    }

    @WorkerThread
    private native void nativeSetCoordinate(
            long jniEegeoMapApiPtr,
            double latitudeDegrees,
            double longitudeDegrees);

    @WorkerThread
    private native void nativeSetElevation(
            long jniEegeoMapApiPtr,
            double elevation);

    @WorkerThread
    private native void nativeSetIndoorMap(
            long jniEegeoMapApiPtr,
            String indoorMapId,
            int floorId);

    @WorkerThread
    private native void nativeSetEnabled(
            long jniEegeoMapApiPtr,
            boolean enabled);

    @WorkerThread
    private native void nativeSetDirection(
            long jniEegeoMapApiPtr,
            double heading);
}


