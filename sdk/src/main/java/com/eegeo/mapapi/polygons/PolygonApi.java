package com.eegeo.mapapi.polygons;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class PolygonApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<Polygon> m_nativeHandleToPolygon = new SparseArray<>();


    public PolygonApi(INativeMessageRunner nativeRunner,
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
    public void register(Polygon polygon, Polygon.AllowHandleAccess allowHandleAccess) {
        m_nativeHandleToPolygon.put(polygon.getNativeHandle(allowHandleAccess), polygon);
    }

    @WorkerThread
    public int create(PolygonOptions polygonOptions, Polygon.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polygon");

        if (polygonOptions.getPoints().size() < 2)
            throw new InvalidParameterException("PolygonOptions points must contain at least two elements");

        List<LatLng> exteriorPoints = polygonOptions.getPoints();
        List<List<LatLng>> holes = polygonOptions.getHoles();

        final int[] ringVertexCounts = buildRingVertexCounts(exteriorPoints, holes);
        final double[] allPointsDoubleArray = buildPointsArray(exteriorPoints, holes, ringVertexCounts);

        return nativeCreatePolygon(
                m_jniEegeoMapApiPtr,
                polygonOptions.getIndoorMapId(),
                polygonOptions.getIndoorFloorId(),
                polygonOptions.getElevation(),
                polygonOptions.getElevationMode().ordinal(),
                allPointsDoubleArray,
                ringVertexCounts,
                polygonOptions.getFillColor()
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
    public void destroy(Polygon polygon, Polygon.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polygon");

        final int nativeHandle = polygon.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToPolygon.get(nativeHandle) != null) {
            nativeDestroyPolygon(m_jniEegeoMapApiPtr,nativeHandle);
            m_nativeHandleToPolygon.remove(nativeHandle);
        }
    }

    @WorkerThread
    void setIndoorMap(int nativeHandle,
                      Polygon.AllowHandleAccess allowHandleAccess,
                      String indoorMapId,
                      int indoorFloorId) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polygon");

        nativeSetIndoorMap(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                indoorMapId,
                indoorFloorId);
    }

    @WorkerThread
    void setElevation(int nativeHandle,
                      Polygon.AllowHandleAccess allowHandleAccess,
                      double elevation,
                      ElevationMode elevationMode) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polygon");

        nativeSetElevation(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                elevation,
                elevationMode.ordinal());
    }

    @WorkerThread
    void setStyleAttributes(int nativeHandle,
                            Polygon.AllowHandleAccess allowHandleAccess,
                            int colorARGB) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Polygon");

        nativeSetStyleAttributes(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                colorARGB);
    }

    @WorkerThread
    private native int nativeCreatePolygon(
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
    private native void nativeDestroyPolygon(
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


