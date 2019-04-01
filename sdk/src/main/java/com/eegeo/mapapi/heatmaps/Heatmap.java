package com.eegeo.mapapi.heatmaps;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.WeightedLatLngAlt;
import com.eegeo.mapapi.polygons.PolygonOptions;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.List;
import java.util.concurrent.Callable;

public class Heatmap extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final HeatmapApi m_heatmapApi;
    private String m_indoorMapId;
    private int m_indoorFloorId;
    private double m_elevation;
    private ElevationMode m_elevationMode;

    private List<LatLng> m_polygonPoints;
    private List<List<LatLng>> m_polygonHoles;

    private List<WeightedLatLngAlt> m_data;
    private double m_weightMin;
    private double m_weightMax;
    private int m_textureWidth;
    private int m_textureHeight;
    private double m_radiusMinMeters;
    private double m_radiusMaxMeters;
    private double m_radiusBlend;
    private double m_intensityScale;
    private double m_opacity;

    private float m_occludedStyleAlpha;
    private float m_occludedStyleSaturation;
    private float m_occludedStyleBrightness;

    private int m_occludedFeatures;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addHeatmap to create a heatmap
     *
     * @eegeo.internal
     */
    @UiThread
    public Heatmap(@NonNull final HeatmapApi heatmapApi,
                  @NonNull final HeatmapOptions heatmapOptions) {
        super(heatmapApi.getNativeRunner(), heatmapApi.getUiRunner(),
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return heatmapApi.create(heatmapOptions, m_allowHandleAccess);
                    }
                });

        PolygonOptions polygonOptions = heatmapOptions.getPolygonOptions();

        m_heatmapApi = heatmapApi;
        m_indoorMapId = polygonOptions.getIndoorMapId();
        m_indoorFloorId = polygonOptions.getIndoorFloorId();
        m_elevation = polygonOptions.getElevation();
        m_elevationMode = polygonOptions.getElevationMode();
        m_polygonPoints = polygonOptions.getPoints();
        m_polygonHoles = polygonOptions.getHoles();

        m_data = heatmapOptions.getData();
        m_weightMin = heatmapOptions.getWeightMin();
        m_weightMax = heatmapOptions.getWeightMax();
        m_textureWidth = heatmapOptions.getTextureWidth();
        m_textureHeight = heatmapOptions.getTextureHeight();
        m_radiusMinMeters = heatmapOptions.getRadiusMinMeters();
        m_radiusMaxMeters = heatmapOptions.getRadiusMaxMeters();
        m_radiusBlend = heatmapOptions.getRadiusBlend();
        m_intensityScale = heatmapOptions.getIntensityScale();
        m_opacity = heatmapOptions.getOpacity();
        m_occludedStyleAlpha = heatmapOptions.getOccludedStyleAlpha();
        m_occludedStyleSaturation = heatmapOptions.getOccludedStyleSaturation();
        m_occludedStyleBrightness = heatmapOptions.getOccludedStyleBrightness();
        m_occludedFeatures = heatmapOptions.getOccludedFeatures();


        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                heatmapApi.register(Heatmap.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the current elevation of the heatmap. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this heatmap.
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  HeatmapOptions.MarkerElevationMode.
     */
    @UiThread
    public void setElevation(double elevation) {
        m_elevation = elevation;
        updateNativeElevation();
    }

    /**
     * Returns the mode specifying how the Elevation property is interpreted.
     *
     * @return An enumerated value indicating whether Elevation is specified as a height above
     * terrain, or an absolute altitude above sea level.
     */
    @UiThread
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Sets the elevation mode for this heatmap.
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property.
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateNativeElevation();
    }

    /**
     * Gets the identifier of an indoor map on which this heatmap should be displayed, if any.
     *
     * @return For a heatmap on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Sets the identifier of an indoor map on which this heatmap should be displayed, if any.
     *
     */
    @UiThread
    public void setIndoorMapId(String indoorMapId) {
        m_indoorMapId = indoorMapId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the identifier of an indoor map floor on which this heatmap should be displayed, if any.
     *
     * @return The indoor map floor id.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Sets the identifier of an indoor map floor on which this heatmap should be displayed, if any.
     *
     * @param indoorFloorId The indoor map floor id.
     */
    @UiThread
    public void setIndoorFloorId(int indoorFloorId) {
        m_indoorFloorId = indoorFloorId;
        updateNativeIndoorMap();
    }

    public double getWeightMin() { return m_weightMin; }

    public double getWeightMax() { return m_weightMax; }

    public double getRadiusBlend() {
        return m_radiusBlend;
    }

    public double getRadiusMinMeters() {
        return m_radiusMinMeters;
    }

    public double getRadiusMaxMeters() {
        return m_radiusMaxMeters;
    }

    public double getIntensityScale() { return m_intensityScale; }

    public double getOpacity() { return m_opacity; }

    public float getOccludedStyleAlpha() { return m_occludedStyleAlpha; }

    public float getOccludedStyleSaturation() { return m_occludedStyleSaturation; }

    public float getOccludedStyleBrightness()  { return m_occludedStyleBrightness; }


    public void setRadiusBlend(double radiusBlend) {
        m_radiusBlend = radiusBlend;
        updateNativeRadiusBlend();
    }

    public void setIntensityScale(double intensityScale) {
        m_intensityScale = intensityScale;
        updateNativeIntensityScale();
    }

    public void setOpacity(double opacity) {
        m_opacity = opacity;
        updateNativeOpacity();
    }

    public void setOccludedStyle(float alpha, float saturation, float brightness) {
        m_occludedStyleAlpha = alpha;
        m_occludedStyleSaturation = saturation;
        m_occludedStyleBrightness = brightness;
        updateNativeOccludedStyle();
    }

    public void setOccludedFeatures(int occludedFeatures) {
        m_occludedFeatures = occludedFeatures;
        updateNativeOccludedStyle();
    }



    /**
     * Gets the outline points of the heatmap polygon.
     *
     * @return The vertices of the exterior ring (outline) of this heatmap.
     */
    @UiThread
    public List<LatLng> getPolygonPoints() {
        return m_polygonPoints;
    }

    /**
     * Gets the points that define holes for the heatmap polygon.
     *
     * @return A list of lists - each inner list contains the vertices of an interior ring (hole) of this heatmap.
     */
    @UiThread
    public List<List<LatLng>> getPolygonHoles() {
        return m_polygonHoles;
    }

    @UiThread
    public List<WeightedLatLngAlt> getData() { return m_data; }

    /**
     * Removes this heatmap from the map and destroys the heatmap. Use EegeoMap.removeHeatmap
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_heatmapApi.destroy(Heatmap.this, Heatmap.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    private void updateNativeIndoorMap() {
        final String indoorMapId = m_indoorMapId;
        final int indoorFloorId = m_indoorFloorId;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setIndoorMap(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        indoorMapId,
                        indoorFloorId);
            }
        });
    }

    @UiThread
    private void updateNativeElevation() {
        final double elevation = m_elevation;
        final ElevationMode elevationMode = m_elevationMode;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setElevation(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        elevation,
                        elevationMode);
            }
        });
    }

    @UiThread
    private void updateNativeRadiusBlend() {
        final double radiusBlend = m_radiusBlend;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setRadiusBlend(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        radiusBlend);
            }
        });
    }

    @UiThread
    private void updateNativeIntensityScale() {
        final double intensityScale = m_intensityScale;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setIntensityScale(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        intensityScale);
            }
        });
    }

    @UiThread
    private void updateNativeOpacity() {
        final double opacity = m_opacity;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setOpacity(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        opacity);
            }
        });
    }

    @UiThread
    private void updateNativeOccludedStyle() {
        final int occludedFeatures = m_occludedFeatures;
        final float alpha = m_occludedStyleAlpha;
        final float saturation = m_occludedStyleSaturation;
        final float brightness = m_occludedStyleBrightness;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setOccludedStyle(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        occludedFeatures,
                        alpha,
                        saturation,
                        brightness);
            }
        });
    }



    @UiThread
    private void updateNativeData() {
        final List<WeightedLatLngAlt> data = m_data;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setData(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        data);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by HeatmapApi");

        if (!hasNativeHandle())
            throw new IllegalStateException("Native handle not available");

        return getNativeHandle();
    }

    static final class AllowHandleAccess {
        @WorkerThread
        private AllowHandleAccess() {
        }
    }
}
