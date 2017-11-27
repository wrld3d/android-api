package com.eegeo.mapapi.buildings;

import android.graphics.Point;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;

import java.security.InvalidParameterException;

/**
 * @eegeo.internal
 */
public class BuildingsApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<BuildingHighlight> m_nativeHandleToBuildingHighlight = new SparseArray<>();

    public BuildingsApi(INativeMessageRunner nativeRunner,
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
    public void register(BuildingHighlight buildingHighlight, BuildingHighlight.AllowHandleAccess allowHandleAccess) {
        int nativeHandle = buildingHighlight.getNativeHandle(allowHandleAccess);
        m_nativeHandleToBuildingHighlight.put(nativeHandle, buildingHighlight);
        fetchBuildingInformation(nativeHandle);
    }

    @WorkerThread
    public int create(BuildingHighlightOptions buildingHighlightOptions, BuildingHighlight.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BuildingHighlight");

        int selectionModeInt = buildingHighlightOptions.getSelectionMode().ordinal();
        LatLng location = buildingHighlightOptions.getSelectionLocation();
        Point screenPoint = buildingHighlightOptions.getSelectionScreenPoint();
        boolean shouldCreateView = buildingHighlightOptions.getShouldCreateView();

        return nativeCreateBuildingHighlight(
                m_jniEegeoMapApiPtr,
                selectionModeInt,
                location.latitude,
                location.longitude,
                screenPoint.x,
                screenPoint.y,
                buildingHighlightOptions.getColor(),
                shouldCreateView);
    }


    @WorkerThread
    public void destroy(BuildingHighlight buildingHighlight, BuildingHighlight.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BuildingHighlight");

        final int nativeHandle = buildingHighlight.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToBuildingHighlight.get(nativeHandle) != null) {
            nativeDestroyBuildingHighlight(m_jniEegeoMapApiPtr, nativeHandle);
            m_nativeHandleToBuildingHighlight.remove(nativeHandle);
        }
    }

    @WorkerThread
    void setStyleAttributes(int nativeHandle,
                            BuildingHighlight.AllowHandleAccess allowHandleAccess,
                            int colorARGB) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by BuildingHighlight");

        nativeSetStyleAttributes(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                colorARGB);
    }

    @WorkerThread
    public void notifyBuildingInformationReceived(final int nativeHandle) {
        if (m_nativeHandleToBuildingHighlight.get(nativeHandle) != null) {
            fetchBuildingInformation(nativeHandle);
        }
    }

    @WorkerThread
    private void fetchBuildingInformation(int nativeHandle)
    {
        final BuildingHighlight buildingHighlight = m_nativeHandleToBuildingHighlight.get(nativeHandle);

        if (buildingHighlight == null)
            throw new NullPointerException("BuildingHighlight object not found for nativeHandle");

        final BuildingInformation buildingInformation = nativeGetBuildingInformation(m_jniEegeoMapApiPtr, nativeHandle);
        if (buildingInformation != null) {
            m_uiRunner.runOnUiThread(new Runnable() {
                @UiThread
                @Override
                public void run() {
                    buildingHighlight.setBuildingInformation(buildingInformation);
                }
            });
        }
    }

    @WorkerThread
    private native int nativeCreateBuildingHighlight(
            long jniEegeoMapApiPtr,
            int selectionMode,
            double latitudeDegrees,
            double longitudeDegrees,
            int screenPointX,
            int screenPointY,
            int colorARGB,
            boolean shouldCreateView
    );

    @WorkerThread
    private native void nativeDestroyBuildingHighlight(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native void nativeSetStyleAttributes(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            int colorARGB);

    @WorkerThread
    private native BuildingInformation nativeGetBuildingInformation(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

}


