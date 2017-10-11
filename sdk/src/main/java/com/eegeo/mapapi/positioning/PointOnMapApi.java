package com.eegeo.mapapi.positioning;


import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class PointOnMapApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<PointOnMap> m_nativeHandleToPointOnMap = new SparseArray<PointOnMap>();


    public PointOnMapApi(INativeMessageRunner nativeRunner,
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
    public void registerPointOnMap(PointOnMap pointOnMap, PointOnMap.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToPointOnMap.put(pointOnMap.getNativeHandle(allowHandleAccess), pointOnMap);
    }

    @WorkerThread
    public void unregisterPointOnMap(PointOnMap pointOnMap, PointOnMap.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToPointOnMap.remove(pointOnMap.getNativeHandle(allowHandleAccess));
    }

    @WorkerThread
    public int createPointOnMap(PointOnMapOptions pointOnMapOptions, PointOnMap.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by PointOnMap");

        LatLng location = pointOnMapOptions.getPosition();
        if (location == null)
            throw new InvalidParameterException("PointOnMapOptions position must be set");

        return nativeCreatePointOnMap(
                m_jniEegeoMapApiPtr,
                location.latitude,
                location.longitude,
                pointOnMapOptions.getElevation(),
                pointOnMapOptions.getElevationMode().ordinal(),
                pointOnMapOptions.getIndoorMapId(),
                pointOnMapOptions.getIndoorFloorId()
        );
    }

    @WorkerThread
    public void destroy(PointOnMap pointOnMap, PointOnMap.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by PointOnMap");

        nativeDestroyPointOnMap(
                m_jniEegeoMapApiPtr,
                pointOnMap.getNativeHandle(allowHandleAccess));
    }

    @WorkerThread
    public void updateLocation(int pointOnMapNativeHandle, PointOnMap.AllowHandleAccess allowHandleAccess, LatLng position, double elevation, ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by PointOnMap");


        nativeUpdateLocation(
                m_jniEegeoMapApiPtr,
                pointOnMapNativeHandle,
                position.latitude,
                position.longitude,
                elevation,
                elevationMode.ordinal());
    }

    @WorkerThread
    private native int nativeCreatePointOnMap(
            long jniEegeoMapApiPtr,
            double latitudeDegrees,
            double longitudeDegrees,
            double anchorHeight,
            int anchorHeightMode,
            String interiorId,
            int interiorFloorId);

    @WorkerThread
    private native void nativeDestroyPointOnMap(
            long jniEegeoMapApiPtr,
            int pointOnMapHandle
    );

    @WorkerThread
    private native void nativeUpdateLocation(
            long jniEegeoMapApiPtr,
            int pointOnMapHandle,
            double latitudeDegrees,
            double longitudeDegrees,
            double elevation,
            int elevationMode);
}


