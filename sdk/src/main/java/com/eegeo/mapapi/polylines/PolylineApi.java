package com.eegeo.mapapi.polylines;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLngHelpers;

import java.security.InvalidParameterException;
import java.util.List;

public class PolylineApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<Polyline> m_nativeHandleToPolyline = new SparseArray<>();


    public PolylineApi(INativeMessageRunner nativeRunner,
                       IUiMessageRunner uiRunner,
                       long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    INativeMessageRunner getNativeRunner() {
        return m_nativeRunner;
    }

    @UiThread
    IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    @WorkerThread
    public void register(Polyline polyline, Polyline.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToPolyline.put(polyline.getNativeHandle(allowHandleAccess), polyline);
    }

    @WorkerThread
    public int create(PolylineOptions polylineOptions, Polyline.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polyline");

        if (polylineOptions.getPoints().size() < 2)
            throw new InvalidParameterException("PolylineOptions points must contain at least two elements");

        double[] latLongs = LatLngHelpers.pointsToArray(polylineOptions.getPoints());

        List<Double> perPointElevationsList = polylineOptions.getPerPointElevations();
        double[] perPointElevations = new double[perPointElevationsList.size()];
        for (int i = 0; i < perPointElevationsList.size(); ++i) {
            perPointElevations[i] = perPointElevationsList.get(i);
        }


        return nativeCreatePolyline(
                m_jniEegeoMapApiPtr,
                polylineOptions.getIndoorMapId(),
                polylineOptions.getIndoorFloorId(),
                polylineOptions.getElevation(),
                polylineOptions.getElevationMode().ordinal(),
                latLongs,
                perPointElevations,
                polylineOptions.getWidth(),
                polylineOptions.getColor(),
                polylineOptions.getMiterLimit()
        );
    }

    @WorkerThread
    public void destroy(Polyline polyline, Polyline.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polyline");

        final int nativeHandle = polyline.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToPolyline.get(nativeHandle) != null) {
            nativeDestroyPolyline(m_jniEegeoMapApiPtr, nativeHandle);
            m_nativeHandleToPolyline.remove(nativeHandle);
        }
    }

    @WorkerThread
    void setIndoorMap(int nativeHandle,
                      Polyline.AllowHandleAccess allowHandleAccess,
                      String indoorMapId,
                      int indoorFloorId) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polyline");

        nativeSetIndoorMap(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                indoorMapId,
                indoorFloorId);
    }

    @WorkerThread
    void setElevation(int nativeHandle,
                      Polyline.AllowHandleAccess allowHandleAccess,
                      double elevation,
                      ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polyline");

        nativeSetElevation(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                elevation,
                elevationMode.ordinal());
    }

    @WorkerThread
    void setStyleAttributes(int nativeHandle,
                            Polyline.AllowHandleAccess allowHandleAccess,
                            float width,
                            int colorARGB,
                            float miterLimit) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polyline");

        nativeSetStyleAttributes(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                width,
                colorARGB,
                miterLimit);
    }

    @WorkerThread
    private native int nativeCreatePolyline(
            long jniEegeoMapApiPtr,
            String indoorMapId,
            int indoorFloorId,
            double elevation,
            int elevationMode,
            double[] points,
            double[] perPointElevations,
            float width,
            int colorARGB,
            float miterLimit
    );

    @WorkerThread
    private native void nativeDestroyPolyline(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native void nativeSetIndoorMap(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            String indoorMapId,
            int indoorFloorId);

    @WorkerThread
    private native void nativeSetElevation(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double elevation,
            int elevationModeInt);

    @WorkerThread
    private native void nativeSetStyleAttributes(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            float width,
            int colorARGB,
            float miterLimit);

}


