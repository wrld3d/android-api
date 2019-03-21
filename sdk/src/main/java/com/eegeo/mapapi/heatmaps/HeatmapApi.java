package com.eegeo.mapapi.heatmaps;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class HeatmapApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<Heatmap> m_nativeHandleToHeatmap = new SparseArray<>();


    public HeatmapApi(INativeMessageRunner nativeRunner,
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
    public void register(Heatmap heatmap, Heatmap.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToHeatmap.put(heatmap.getNativeHandle(allowHandleAccess), heatmap);
    }

    @WorkerThread
    public int create(HeatmapOptions heatmapOptions, Heatmap.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        if (heatmapOptions.getPoints().size() < 2)
            throw new InvalidParameterException("HeatmapOptions points must contain at least two elements");

        List<LatLng> exteriorPoints = heatmapOptions.getPoints();
        List<List<LatLng>> holes = heatmapOptions.getHoles();

        final int[] ringVertexCounts = buildRingVertexCounts(exteriorPoints, holes);
        final double[] allPointsDoubleArray = buildPointsArray(exteriorPoints, holes, ringVertexCounts);

        return nativeCreateHeatmap(
                m_jniEegeoMapApiPtr,
                heatmapOptions.getIndoorMapId(),
                heatmapOptions.getIndoorFloorId(),
                heatmapOptions.getElevation(),
                heatmapOptions.getElevationMode().ordinal(),
                allPointsDoubleArray,
                ringVertexCounts,
                heatmapOptions.getFillColor()
        );
    }

    private double[] buildPointsArray(List<LatLng> exteriorPoints, List<List<LatLng>> holes, int[] ringVertexCounts) {
        final int totalVertexCount = getTotalVertexCount(ringVertexCounts);

        List<LatLng> allPoints = new ArrayList<>(totalVertexCount);
        allPoints.addAll(exteriorPoints);
        for (List<LatLng> hole : holes) {
            allPoints.addAll(hole);
        }

        return pointsToDoubleArray(allPoints);
    }

    private int getTotalVertexCount(int[] ringVertexCounts) {
        int totalVertexCount = 0;
        for (int vertexCount : ringVertexCounts) {
            totalVertexCount += vertexCount;
        }
        return totalVertexCount;
    }

    private int[] buildRingVertexCounts(List<LatLng> exteriorPoints, List<List<LatLng>> holes) {
        final int allRingsCount = holes.size() + 1;
        final int[] ringVertexCounts = new int[allRingsCount];
        ringVertexCounts[0] = exteriorPoints.size();
        for (int holeIndex = 0; holeIndex < holes.size(); ++holeIndex) {
            ringVertexCounts[holeIndex + 1] = holes.get(holeIndex).size();
        }
        return ringVertexCounts;
    }

    private double[] pointsToDoubleArray(List<LatLng> points) {
        final int pointCount = points.size();
        double[] coords = new double[pointCount * 2];
        for (int i = 0; i < pointCount; ++i) {
            coords[i * 2] = points.get(i).latitude;
            coords[i * 2 + 1] = points.get(i).longitude;
        }
        return coords;
    }

    @WorkerThread
    public void destroy(Heatmap heatmap, Heatmap.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        final int nativeHandle = heatmap.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToHeatmap.get(nativeHandle) != null) {
            nativeDestroyHeatmap(m_jniEegeoMapApiPtr,nativeHandle);
            m_nativeHandleToHeatmap.remove(nativeHandle);
        }
    }

    @WorkerThread
    void setIndoorMap(int nativeHandle,
                      Heatmap.AllowHandleAccess allowHandleAccess,
                      String indoorMapId,
                      int indoorFloorId) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeSetIndoorMap(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                indoorMapId,
                indoorFloorId);
    }

    @WorkerThread
    void setElevation(int nativeHandle,
                      Heatmap.AllowHandleAccess allowHandleAccess,
                      double elevation,
                      ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeSetElevation(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                elevation,
                elevationMode.ordinal());
    }

    @WorkerThread
    void setStyleAttributes(int nativeHandle,
                            Heatmap.AllowHandleAccess allowHandleAccess,
                            int colorARGB) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeSetStyleAttributes(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                colorARGB);
    }

    @WorkerThread
    private native int nativeCreateHeatmap(
            long jniEegeoMapApiPtr,
            String indoorMapId,
            int indoorFloorId,
            double elevation,
            int elevationMode,
            double[] points,
            int[] ringVertexCounts,
            int colorARGB
    );

    @WorkerThread
    private native void nativeDestroyHeatmap(
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
            int colorARGB);

}
