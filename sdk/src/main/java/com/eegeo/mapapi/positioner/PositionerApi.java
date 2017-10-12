package com.eegeo.mapapi.positioner;

import android.graphics.Point;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class PositionerApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<Positioner> m_nativeHandleToPositioner = new SparseArray<Positioner>();


    public PositionerApi(INativeMessageRunner nativeRunner,
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
    public void registerPositioner(Positioner positioner, Positioner.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToPositioner.put(positioner.getNativeHandle(allowHandleAccess), positioner);
    }

    @WorkerThread
    public void unregisterPositioner(Positioner positioner, Positioner.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToPositioner.remove(positioner.getNativeHandle(allowHandleAccess));
    }

    @WorkerThread
    public int createPositioner(PositionerOptions positionerOptions, Positioner.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Positioner");

        LatLng location = positionerOptions.getPosition();
        if (location == null)
            throw new InvalidParameterException("PositionerOptions position must be set");

        return nativeCreatePositioner(
                m_jniEegeoMapApiPtr,
                location.latitude,
                location.longitude,
                positionerOptions.getElevation(),
                positionerOptions.getElevationMode().ordinal(),
                positionerOptions.getIndoorMapId(),
                positionerOptions.getIndoorFloorId()
        );
    }

    @WorkerThread
    public void destroy(Positioner positioner, Positioner.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Positioner");

        nativeDestroyPositioner(
                m_jniEegeoMapApiPtr,
                positioner.getNativeHandle(allowHandleAccess));
    }

    @WorkerThread
    public void updateLocation(int positionerNativeHandle, Positioner.AllowHandleAccess allowHandleAccess, LatLng position, double elevation, ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Positioner");


        nativeUpdateLocation(
                m_jniEegeoMapApiPtr,
                positionerNativeHandle,
                position.latitude,
                position.longitude,
                elevation,
                elevationMode.ordinal());
    }

    @WorkerThread
    public Point getScreenPoint(
        int positionerNativeHandle,
        Positioner.AllowHandleAccess allowHandleAccess
    ){
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Positioner");


        return nativeGetScreenPoint(
                m_jniEegeoMapApiPtr,
                positionerNativeHandle
                );
    }

    @WorkerThread
    public void notifyProjectionChanged() {
        for(int i = 0; i < m_nativeHandleToPositioner.size(); i++)
        {
            final int positionerNativeHandle = m_nativeHandleToPositioner.keyAt(i);
            final Point screenPoint = nativeGetScreenPoint(m_jniEegeoMapApiPtr, positionerNativeHandle);
            final Positioner positioner = m_nativeHandleToPositioner.get(positionerNativeHandle);
            m_uiRunner.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    positioner.setScreenPoint(screenPoint);
                }
            });
        }
    }

    @WorkerThread
    private native int nativeCreatePositioner(
            long jniEegeoMapApiPtr,
            double latitudeDegrees,
            double longitudeDegrees,
            double anchorHeight,
            int anchorHeightMode,
            String interiorId,
            int interiorFloorId);

    @WorkerThread
    private native void nativeDestroyPositioner(
            long jniEegeoMapApiPtr,
            int positionerHandle
    );

    @WorkerThread
    private native void nativeUpdateLocation(
            long jniEegeoMapApiPtr,
            int positionerHandle,
            double latitudeDegrees,
            double longitudeDegrees,
            double elevation,
            int elevationMode);

    @WorkerThread
    private native Point nativeGetScreenPoint(
            long jniEegeoMapApiPtr,
            int positionerHandle);
}
