package com.eegeo.mapapi.heatmaps;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.WeightedLatLngAlt;
import com.eegeo.mapapi.polygons.PolygonOptions;

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

        PolygonOptions polygonOptions = heatmapOptions.getPolygonOptions();

        if (polygonOptions.getPoints().size() < 2)
            throw new InvalidParameterException("PolygonOptions points must contain at least two elements");

        // todo_heatmap DRY - factor out commonality with PolygonApi
        List<LatLng> exteriorPoints = polygonOptions.getPoints();
        List<List<LatLng>> holes = polygonOptions.getHoles();

        final int[] ringVertexCounts = buildRingVertexCounts(exteriorPoints, holes);
        final double[] allPointsDoubleArray = buildPointsArray(exteriorPoints, holes, ringVertexCounts);
        final double[] dataDoubleArray = weightedPointsToDoubleArray(heatmapOptions.getWeightedPoints());
        final double weightMin = heatmapOptions.getWeightMin();
        final double weightMax = heatmapOptions.getWeightMax();
        final int resolutionPixels = heatmapOptions.getResolutionPixels();
        final double radiusMinMeters = heatmapOptions.getRadiusMinMeters();
        final double radiusMaxMeters = heatmapOptions.getRadiusMaxMeters();
        final float radiusBlend = heatmapOptions.getRadiusBlend();
        final float opacity = heatmapOptions.getOpacity();
        final float intensityBias = heatmapOptions.getIntensityBias();
        final float intensityScale = heatmapOptions.getIntensityScale();
        final int occludedFeatures = heatmapOptions.getOccludedFeatures();
        final float occludedAlpha = heatmapOptions.getOccludedStyleAlpha();
        final float occludedSaturation = heatmapOptions.getOccludedStyleSaturation();
        final float occludedBrightness = heatmapOptions.getOccludedStyleBrightness();
        final float[] gradientStartParamsArray = heatmapOptions.getGradientStartParams();
        final int[] gradientColorsArray = heatmapOptions.getGradientColors();


        return nativeCreateHeatmap(
                m_jniEegeoMapApiPtr,
                polygonOptions.getIndoorMapId(),
                polygonOptions.getIndoorFloorId(),
                polygonOptions.getElevation(),
                polygonOptions.getElevationMode().ordinal(),
                allPointsDoubleArray,
                ringVertexCounts,
                polygonOptions.getFillColor(),
                dataDoubleArray,
                weightMin,
                weightMax,
                resolutionPixels,
                radiusMinMeters,
                radiusMaxMeters,
                radiusBlend,
                opacity,
                intensityBias,
                intensityScale,
                occludedFeatures,
                occludedAlpha,
                occludedSaturation,
                occludedBrightness,
                gradientStartParamsArray,
                gradientColorsArray
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


    private double[] weightedPointsToDoubleArray(List<WeightedLatLngAlt> weightedPoints) {
        final int elementCount = weightedPoints.size();
        final int doublesPerElement = 4;
        double[] doubles = new double[elementCount * doublesPerElement];
        for (int i = 0; i < elementCount; ++i) {
            WeightedLatLngAlt element = weightedPoints.get(i);
            doubles[i * doublesPerElement + 0] = element.point.latitude;
            doubles[i * doublesPerElement + 1] = element.point.longitude;
            doubles[i * doublesPerElement + 2] = element.point.altitude;
            doubles[i * doublesPerElement + 3] = element.intensity;
        }
        return doubles;
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
    void setRadiusBlend(int nativeHandle,
                        Heatmap.AllowHandleAccess allowHandleAccess,
                        double radiusBlend) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeRadiusBlend(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                radiusBlend);
    }

    @WorkerThread
    void setIntensityBiasScale(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            float intensityBias,
            float intensityScale
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeIntensityBiasScale(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                intensityBias,
                intensityScale
        );
    }

    @WorkerThread
    void setOpacity(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            double opacity
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeOpacity(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                (float)opacity);
    }

    @WorkerThread
    void setGradient(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            float[] gradientStartParams,
            int[] gradientColors

    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeGradient(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                gradientStartParams,
                gradientColors);
    }


    @WorkerThread
    void setOccludedStyle(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            int occludedFeatures,
            float occludedAlpha,
            float occludedSaturation,
            float occludedBrightness
        ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeOccludedStyle(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                occludedFeatures,
                occludedAlpha,
                occludedSaturation,
                occludedBrightness
        );
    }

    @WorkerThread
    void setResolution(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            int resolutionPixels
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeResolution(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                resolutionPixels
        );
    }

    @WorkerThread
    void setRadiusExtents(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            double radiusMinMeters,
            double radiusMaxMeters
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeRadiusExtents(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                radiusMinMeters,
                radiusMaxMeters
        );
    }

    @WorkerThread
    void setData(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            List<WeightedLatLngAlt> weightedPoints,
            double weightMin,
            double weightMax
            ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        double[] weightedPointsDoubleArray = weightedPointsToDoubleArray(weightedPoints);
        nativeSetData(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                weightedPointsDoubleArray,
                weightMin,
                weightMax);
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
            int colorARGB,
            double[] dataDoubleArray,
            double weightMin,
            double weightMax,
            int resolutionPixels,
            double radiusMinMeters,
            double radiusMaxMeters,
            float radiusBlend,
            float opacity,
            float intensityBias,
            float intensityScale,
            int occludedFeatures,
            float occludedAlpha,
            float occludedSaturation,
            float occludedBrightness,
            float[] gradientStartParams,
            int[] gradientColors
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
            int indoorFloorId
    );

    @WorkerThread
    private native void nativeSetElevation(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double elevation,
            int elevationModeInt
    );

    @WorkerThread
    private native void nativeRadiusBlend(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double radiusBlend);

    @WorkerThread
    private native void nativeIntensityBiasScale(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            float intensityBias,
            float intensityScale
    );

    @WorkerThread
    private native void nativeOpacity(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            float opacity
    );

    @WorkerThread
    private native void nativeGradient(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            float[] gradientStartParams,
            int[] gradientColors
    );

    @WorkerThread
    private native void nativeOccludedStyle(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            int occludedFeatures,
            float occludedAlpha,
            float occludedSaturation,
            float occludedBrightness
    );

    @WorkerThread
    private native void nativeResolution(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            int resolutionPixels
    );

    @WorkerThread
    private native void nativeRadiusExtents(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double radiusMinMeters,
            double radiusMaxMeters
    );

    @WorkerThread
    private native void nativeSetData(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double[] weightedPointsDoubleArray,
            double weightMin,
            double weightMax
            );
}
