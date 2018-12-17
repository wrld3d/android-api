package com.eegeo.mapapi.props;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;
import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;
import java.security.InvalidParameterException;

public class PropsApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<Prop> m_nativeHandleToProp = new SparseArray<>();

    @WorkerThread
    public void register(Prop prop, Prop.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToProp.put(prop.getNativeHandle(allowHandleAccess), prop);
    }

    public PropsApi(INativeMessageRunner nativeRunner,
                      IUiMessageRunner uiRunner,
                      long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @WorkerThread
    public int create(PropOptions propOptions, Prop.AllowHandleAccess allowHandleAccess) throws InvalidParameterException, NullPointerException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        if (propOptions.getIndoorMapId().isEmpty())
            throw new InvalidParameterException("PropOptions must specify an indoor map Id");

        if (propOptions.getGeometryId().isEmpty())
            throw new InvalidParameterException("PropOptions must specify a geometry Id");

        return nativeCreateProp(
                m_jniEegeoMapApiPtr,
                propOptions.getIndoorMapId(),
                propOptions.getIndoorFloorId(),
                propOptions.getName(),
                propOptions.getPosition().latitude,
                propOptions.getPosition().longitude,
                propOptions.getElevation(),
                propOptions.getElevationMode().ordinal(),
                propOptions.getHeadingDegrees(),
                propOptions.getGeometryId());
    }

    @WorkerThread
    public void destroy(Prop prop, Prop.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        final int nativeHandle = prop.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToProp.get(nativeHandle) != null) {
            nativeDestroyProp(m_jniEegeoMapApiPtr, nativeHandle);
            m_nativeHandleToProp.remove(nativeHandle);
        }
    }

    @WorkerThread
    public void setPosition(int nativeHandle, Prop.AllowHandleAccess allowHandleAccess, LatLng position) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        nativeSetLocation(m_jniEegeoMapApiPtr, nativeHandle, position.latitude, position.longitude);
    }

    @WorkerThread
    public void setHeadingDegrees(int nativeHandle, Prop.AllowHandleAccess allowHandleAccess, double headingDegrees) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        nativeSetHeadingDegrees(m_jniEegeoMapApiPtr, nativeHandle, headingDegrees);
    }

    @WorkerThread
    public void setGeometryId(int nativeHandle, Prop.AllowHandleAccess allowHandleAccess, String geometryId) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        nativeSetGeometryId(m_jniEegeoMapApiPtr, nativeHandle, geometryId);
    }

    @WorkerThread
    public void setElevation(int nativeHandle, Prop.AllowHandleAccess allowHandleAccess, double elevation) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        nativeSetElevation(m_jniEegeoMapApiPtr, nativeHandle, elevation);
    }

    @WorkerThread
    public void setElevationMode(int nativeHandle, Prop.AllowHandleAccess allowHandleAccess, ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Prop");

        nativeSetElevationMode(m_jniEegeoMapApiPtr, nativeHandle, elevationMode.ordinal());
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
    private native int nativeCreateProp(
            long jniEegeoMapApiPtr,
            String indoorMapId,
            int indoorFloorId,
            String name,
            double latitudeDegrees,
            double longitudeDegrees,
            double elevation,
            int elevationMode,
            double headingDegrees,
            String geometryId
    );

    @WorkerThread
    private native void nativeDestroyProp(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native void nativeSetHeadingDegrees(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double headingDegrees);

    @WorkerThread
    private native void nativeSetGeometryId(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            String geometryId);

    @WorkerThread
    private native void nativeSetElevation(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double elevation);

    @WorkerThread
    private native void nativeSetLocation(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double latitudeDegrees,
            double longitudeDegrees);

    @WorkerThread
    private native void nativeSetElevationMode(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            int elevationModeInt);
}
