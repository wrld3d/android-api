package com.eegeo.mapapi.markers;


import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class MarkerApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private ArrayList<OnMarkerClickListener> m_onMarkerClickListeners = new ArrayList<OnMarkerClickListener>();
    private SparseArray<Marker> m_nativeHandleToMarker = new SparseArray<Marker>();


    public MarkerApi(INativeMessageRunner nativeRunner,
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
    public void registerMarker(Marker marker, Marker.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToMarker.put(marker.getNativeHandle(allowHandleAccess), marker);
    }

    @WorkerThread
    public void unregisterMarker(Marker marker, Marker.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToMarker.remove(marker.getNativeHandle(allowHandleAccess));
    }

    @UiThread
    public void addMarkerClickListener(OnMarkerClickListener listener) {
        m_onMarkerClickListeners.add(listener);
    }

    @UiThread
    public void removeMarkerClickListener(OnMarkerClickListener listener) {
        m_onMarkerClickListeners.remove(listener);
    }

    @WorkerThread
    public int createMarker(MarkerOptions markerOptions, Marker.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Marker");

        LatLng location = markerOptions.getPosition();
        if (location == null)
            throw new InvalidParameterException("MarkerOptions position must be set");

        return nativeCreateMarker(
                m_jniEegeoMapApiPtr,
                location.latitude,
                location.longitude,
                markerOptions.getElevation(),
                markerOptions.getElevationMode().ordinal(),
                markerOptions.getIndoorMapId(),
                markerOptions.getIndoorFloorId(),
                markerOptions.getTitle(),
                markerOptions.getIconKey(),
                markerOptions.getUserData(),
                markerOptions.getDrawOrder()
        );
    }

    @WorkerThread
    public void destroy(Marker marker, Marker.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Marker");

        nativeDestroyMarker(
                m_jniEegeoMapApiPtr,
                marker.getNativeHandle(allowHandleAccess));
    }

    @WorkerThread
    public void updateLocation(int markerNativeHandle, Marker.AllowHandleAccess allowHandleAccess, LatLng position, double elevation, ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Marker");


        nativeUpdateLocation(
                m_jniEegeoMapApiPtr,
                markerNativeHandle,
                position.latitude,
                position.longitude,
                elevation,
                elevationMode.ordinal());
    }

    @WorkerThread
    public void updateLabel(int markerNativeHandle, Marker.AllowHandleAccess allowHandleAccess, String title, String iconKey, int drawOrder) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Marker");

        nativeUpdateLabel(
                m_jniEegeoMapApiPtr,
                markerNativeHandle,
                title,
                iconKey,
                drawOrder);
    }

    @WorkerThread
    public void notifyMarkerClicked(final int markerId) {
        if (m_nativeHandleToMarker.get(markerId) != null) {
            final Marker marker = m_nativeHandleToMarker.get(markerId);

            m_uiRunner.runOnUiThread(new Runnable() {
                @UiThread
                @Override
                public void run() {
                    for (OnMarkerClickListener listener : m_onMarkerClickListeners) {
                        listener.onMarkerClick(marker);
                    }
                }
            });
        }

    }

    @WorkerThread
    private native int nativeCreateMarker(
            long jniEegeoMapApiPtr,
            double latitudeDegrees,
            double longitudeDegrees,
            double anchorHeight,
            int anchorHeightMode,
            String interiorId,
            int interiorFloorId,
            String labelText,
            String iconKey,
            String entityName,
            int drawOrder);

    @WorkerThread
    private native void nativeDestroyMarker(
            long jniEegeoMapApiPtr,
            int markerHandle
    );

    @WorkerThread
    private native void nativeUpdateLocation(
            long jniEegeoMapApiPtr,
            int markerHandle,
            double latitudeDegrees,
            double longitudeDegrees,
            double elevation,
            int elevationMode);

    @WorkerThread
    private native void nativeUpdateLabel(
            long jniEegeoMapApiPtr,
            int markerHandle,
            String labelText,
            String iconKey,
            int drawOrder
    );
}


