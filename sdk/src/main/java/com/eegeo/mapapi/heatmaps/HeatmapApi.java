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

        if (!polygonOptions.getPoints().isEmpty() && polygonOptions.getPoints().size() < 3)
            throw new InvalidParameterException("PolygonOptions points must either be empty or contain at least three elements");

        // todo_heatmap DRY - factor out commonality with PolygonApi
        List<LatLng> exteriorPoints = polygonOptions.getPoints();
        List<List<LatLng>> holes = polygonOptions.getHoles();

        final int[] ringVertexCounts = buildRingVertexCounts(exteriorPoints, holes);
        final double[] allPointsDoubleArray = buildPointsArray(exteriorPoints, holes, ringVertexCounts);
        final double[] dataDoubleArray = weightedPointsToDoubleArray(heatmapOptions.getWeightedPoints());
        final double weightMin = heatmapOptions.getWeightMin();
        final double weightMax = heatmapOptions.getWeightMax();
        final int resolutionPixels = heatmapOptions.getResolutionPixels();
        final float textureBorderPercent = heatmapOptions.getTextureBorderPercent();
        final float[] heatmapDensityStops = heatmapOptions.getHeatmapDensityStops();
        final double[] heatmapRadiiArray = heatmapOptions.getHeatmapRadii();
        final double[] heatmapGainsArray = heatmapOptions.getHeatmapGains();
        final boolean useApproximation = heatmapOptions.getUseApproximation();
        final float densityBlend = heatmapOptions.getDensityBlend();
        final boolean interpolateDensityByZoom = heatmapOptions.getInterpolateDensityByZoom();
        final double zoomMin = heatmapOptions.getZoomMin();
        final double zoomMax = heatmapOptions.getZoomMax();
        final float opacity = heatmapOptions.getOpacity();
        final float intensityBias = heatmapOptions.getIntensityBias();
        final float intensityScale = heatmapOptions.getIntensityScale();
        final int occludedFeaturesInt = occludedMapFeaturesToInt(heatmapOptions.getOccludedFeatures());
        final float occludedAlpha = heatmapOptions.getOccludedStyleAlpha();
        final float occludedSaturation = heatmapOptions.getOccludedStyleSaturation();
        final float occludedBrightness = heatmapOptions.getOccludedStyleBrightness();
        final float[] gradientStopsArray = heatmapOptions.getGradientStops();
        final int[] gradientColorsArray = heatmapOptions.getGradientColors();


        return nativeCreateHeatmap(
                m_jniEegeoMapApiPtr,
                polygonOptions.getIndoorMapId(),
                polygonOptions.getIndoorFloorId(),
                polygonOptions.getElevation(),
                polygonOptions.getElevationMode().ordinal(),
                allPointsDoubleArray,
                ringVertexCounts,
                dataDoubleArray,
                weightMin,
                weightMax,
                resolutionPixels,
                textureBorderPercent,
                heatmapDensityStops,
                heatmapRadiiArray,
                heatmapGainsArray,
                useApproximation,
                densityBlend,
                interpolateDensityByZoom,
                zoomMin,
                zoomMax,
                opacity,
                intensityBias,
                intensityScale,
                occludedFeaturesInt,
                occludedAlpha,
                occludedSaturation,
                occludedBrightness,
                gradientStopsArray,
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

        return latLngsToDoubleArray(allPoints);
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

    private double[] latLngsToDoubleArray(List<LatLng> points) {
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
    void setDensityBlend(int nativeHandle,
                        Heatmap.AllowHandleAccess allowHandleAccess,
                        double densityBlend) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeDensityBlend(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                densityBlend);
    }

    @WorkerThread
    void setInterpolateDensityByZoom(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            boolean interpolateDensityByZoom,
            double zoomMin,
            double zoomMax
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeInterpolateDensityByZoom(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                interpolateDensityByZoom,
                zoomMin,
                zoomMax);
    }


    @WorkerThread
    void setIntensityBias(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            float intensityBias
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeIntensityBias(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                intensityBias
        );
    }

    @WorkerThread
    void setIntensityScale(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            float intensityScale
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeIntensityScale(
                m_jniEegeoMapApiPtr,
                nativeHandle,
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
            float[] gradientStops,
            int[] gradientColors

    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeGradient(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                gradientStops,
                gradientColors);
    }

    @WorkerThread
    private int occludedMapFeaturesToInt(HeatmapOcclusionMapFeature[] occludedMapFeatures) {
        int occludedMapFeaturesInt = 0;
        for (HeatmapOcclusionMapFeature occlusionFeature : occludedMapFeatures) {
            switch (occlusionFeature) {
                case ground:
                    occludedMapFeaturesInt |= 0x1;
                case buildings:
                    occludedMapFeaturesInt |= 0x2;
                case trees:
                    occludedMapFeaturesInt |= 0x4;
                case transport:
                    occludedMapFeaturesInt |= 0x8;
            }
        }

        return occludedMapFeaturesInt;
    }

    @WorkerThread
    void setOccludedStyle(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            HeatmapOcclusionMapFeature[] occludedFeatures,
            float occludedAlpha,
            float occludedSaturation,
            float occludedBrightness
        ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        final int occludedFeaturesInt = occludedMapFeaturesToInt(occludedFeatures);

        nativeOccludedStyle(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                occludedFeaturesInt,
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
    void setHeatmapDensities(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            float[] heatmapDensityStops,
            double[] heatmapRadii,
            double[] heatmapGains
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeHeatmapDensities(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                heatmapDensityStops,
                heatmapRadii,
                heatmapGains
        );
    }

    @WorkerThread
    void setUseApproximation(
            int nativeHandle,
            Heatmap.AllowHandleAccess allowHandleAccess,
            boolean useApproximation
    ) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by Heatmap");

        nativeUseApproximation(
                m_jniEegeoMapApiPtr,
                nativeHandle,
                useApproximation
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
            double[] dataDoubleArray,
            double weightMin,
            double weightMax,
            int resolutionPixels,
            float textureBorderPercent,
            float[] heatmapDensityStops,
            double[] heatmapRadii,
            double[] heatmapGains,
            boolean useApproximation,
            float densityBlend,
            boolean interpolateDensityByZoom,
            double zoomMin,
            double zoomMax,
            float opacity,
            float intensityBias,
            float intensityScale,
            int occludedFeatures,
            float occludedAlpha,
            float occludedSaturation,
            float occludedBrightness,
            float[] gradientStops,
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
    private native void nativeDensityBlend(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            double densityBlend);

    @WorkerThread
    private native void nativeInterpolateDensityByZoom(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            boolean nterpolateDensityByZoom,
            double zoomMin,
            double zoomMax);

    @WorkerThread
    private native void nativeIntensityBias(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            float intensityBias
    );

    @WorkerThread
    private native void nativeIntensityScale(
            long jniEegeoMapApiPtr,
            int nativeHandle,
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
            float[] gradientStops,
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
    private native void nativeHeatmapDensities(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            float[] heatmapDensityStops,
            double[] heatmapRadii,
            double[] heatmapGains
    );

    @WorkerThread
    private native void nativeUseApproximation(
            long jniEegeoMapApiPtr,
            int nativeHandle,
            boolean useApproximation
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
