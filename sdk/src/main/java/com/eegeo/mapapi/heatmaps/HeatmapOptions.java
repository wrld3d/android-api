package com.eegeo.mapapi.heatmaps;

import com.eegeo.mapapi.geometry.WeightedLatLngAlt;
import com.eegeo.mapapi.polygons.PolygonOptions;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines creation parameters for a Heatmap.
 */
@SuppressWarnings("WeakerAccess")
public final class HeatmapOptions {
    private PolygonOptions m_polygonOptions = new PolygonOptions();
    private List<WeightedLatLngAlt> m_weightedPoints = new ArrayList<>();
    private double m_weightMin = 0.0;
    private double m_weightMax = 1.0;

    private int m_resolutionPixels = 512;
    private float m_textureBorderPercent = 0.05f;
    private List<Float> m_heatmapDensityStops = new ArrayList<>();
    private List<Double> m_heatmapRadii = new ArrayList<>();
    private List<Double> m_heatmapGains = new ArrayList<>();
    private boolean m_useApproximation = true;
    private float m_densityBlend = 0.0f;
    private boolean m_interpolateDensityByZoom = false;
    private double m_zoomMin = 15.0;
    private double m_zoomMax = 18.0;
    private float m_opacity = 1.f;
    private float m_intensityBias = 0.0f;
    private float m_intensityScale = 1.0f;
    private float m_occludedAlpha = 0.85f;
    private float m_occludedSaturation = 0.7f;
    private float m_occludedBrightness = 0.7f;
    private HeatmapOcclusionMapFeature[] m_occludedFeatures = {HeatmapOcclusionMapFeature.buildings, HeatmapOcclusionMapFeature.trees};
    // http://colorbrewer2.org/#type=sequential&scheme=OrRd&n=5
    // with additional ramp to transparent white below 20%
    private float[] m_gradientStops = {0.f, 0.2f, 0.4f, 0.6f, 0.8f, 1.f};
    private int[] m_gradientColors = {0xffffff00, 0xfef0d9ff, 0xfdcc8aff, 0xfc8d59ff, 0xe34a33ff, 0xb30000ff};

    public static int RESOLUTION_PIXELS_MIN = 32;
    public static int RESOLUTION_PIXELS_MAX = 2048;

    /**
     * Default constructor for heatmap creation parameters.
     */
    public HeatmapOptions() {

    }

    public HeatmapOptions polygon(PolygonOptions polygonOptions) {
        this.m_polygonOptions = polygonOptions;
        return this;
    }

    /**
     * Adds data points to the heatmap
     *
     * @param data The data points to add.
     * @return The HeatmapOptions object on which the method was called, with the new points added.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions add(WeightedLatLngAlt... data) {
        Collections.addAll(m_weightedPoints, data);
        return this;
    }

    /**
     * Adds a vertex to the end of the heatmap.
     *
     * @param data The point to add.
     * @return The HeatmapOptions object on which the method was called, with the new point added.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions add(WeightedLatLngAlt data) {
        m_weightedPoints.add(data);
        return this;
    }

    public HeatmapOptions heatmapRadius(double heatmapRadiusMeters) {
        m_heatmapDensityStops.clear();
        m_heatmapRadii.clear();
        m_heatmapGains.clear();
        m_heatmapDensityStops.add(0.0f);
        m_heatmapRadii.add(heatmapRadiusMeters);
        m_heatmapGains.add(1.0);
        return this;
    }

    public HeatmapOptions addDensityStop(float stop, double heatmapRadiusMeters, double heatmapGain) {
        m_heatmapDensityStops.add(stop);
        m_heatmapRadii.add(heatmapRadiusMeters);
        m_heatmapGains.add(heatmapGain);
        return this;
    }

    public HeatmapOptions setDensityStops(float[] stops, double[] heatmapRadiiMeters, double[] heatmapGains) {
        m_heatmapDensityStops.clear();
        m_heatmapRadii.clear();
        m_heatmapGains.clear();

        if (stops.length == 0) {
            throw new InvalidParameterException("stops must not be empty");
        }

        if (heatmapRadiiMeters.length != stops.length) {
            throw new InvalidParameterException("heatmapRadiiMeters and stops must be equal length");
        }

        if (heatmapGains.length != stops.length) {
            throw new InvalidParameterException("heatmapGains and stops must be equal length");
        }

        for (int i = 0; i < stops.length; ++i) {
            m_heatmapDensityStops.add(stops[i]);
            m_heatmapRadii.add(heatmapRadiiMeters[i]);
            m_heatmapGains.add(heatmapGains[i]);
        }
        return this;
    }

    public HeatmapOptions useApproximation(boolean enableApproximation) {
        m_useApproximation = enableApproximation;
        return this;
    }

    public HeatmapOptions weightMin(double weightMin) {
        m_weightMin = weightMin;
        return this;
    }

    public HeatmapOptions weightMax(double weightMax) {
        m_weightMax = weightMax;
        return this;
    }

    public HeatmapOptions resolution(int resolutionPixels) {
        m_resolutionPixels = Math.min(
            Math.max(resolutionPixels, HeatmapOptions.RESOLUTION_PIXELS_MIN),
            HeatmapOptions.RESOLUTION_PIXELS_MAX
        );
        return this;
    }

    public HeatmapOptions textureBorder(float textureBorderPercent) {
        m_textureBorderPercent = Math.min(
            Math.max(textureBorderPercent, 0.0f),
            0.5f
        );
        return this;
    }


    public HeatmapOptions densityBlend(float densityBlend) {
        m_densityBlend = Math.min(Math.max(densityBlend, 0.0f), 1.0f);
        m_interpolateDensityByZoom = false;
        return this;
    }

    public HeatmapOptions interpolateDensityByZoom(double zoomMin, double zoomMax) {
        m_interpolateDensityByZoom = true;
        m_zoomMin = Math.max(zoomMin, 0.0);
        m_zoomMax = Math.max(zoomMax, 0.0);
        return this;
    }

    public HeatmapOptions opacity(float opacity) {
        m_opacity = Math.min(Math.max(opacity, 0.0f), 1.0f);
        return this;
    }

    public HeatmapOptions intensityBias(float intensityBias) {
        m_intensityBias = Math.min(Math.max(intensityBias, 0.0f), 1.0f);;
        return this;
    }

    public HeatmapOptions intensityScale(float intensityScale) {
        m_intensityScale = Math.max(intensityScale, 0.0f);
        return this;
    }

    public HeatmapOptions occludedFeatures(HeatmapOcclusionMapFeature[] occludedFeatures) {
        m_occludedFeatures = occludedFeatures;
        return this;
    }

    public HeatmapOptions occludedStyleAlpha(float occludedAlpha) {
        m_occludedAlpha = Math.min(Math.max(occludedAlpha, 0.0f), 1.0f);
        return this;
    }

    public HeatmapOptions occludedStyleSaturation(float occludedSaturation) {
        m_occludedSaturation = Math.min(Math.max(occludedSaturation, 0.0f), 1.0f);;
        return this;
    }

    public HeatmapOptions occludedStyleBrightness(float occludedBrightness) {
        m_occludedBrightness = Math.min(Math.max(occludedBrightness, 0.0f), 1.0f);;
        return this;
    }

    public HeatmapOptions gradient(float[] stops, int[] colors) {
        if (stops.length != colors.length) {
            throw new InvalidParameterException("gradient stops and colors arrays must be equal length");
        }
        m_gradientStops = stops;
        m_gradientColors = colors;
        return this;
    }

    public PolygonOptions getPolygonOptions() {
        return m_polygonOptions;
    }


    /**
     * Returns the points for this HeatmapOptions object.
     *
     * @return A list of LatLng objects specifying the heatmap's vertices.
     */
    public List<WeightedLatLngAlt> getWeightedPoints() {
        return m_weightedPoints;
    }

    public double getWeightMin() { return m_weightMin; }

    public double getWeightMax() { return m_weightMax; }

    public int getResolutionPixels() { return m_resolutionPixels; }

    public float getTextureBorderPercent() { return m_textureBorderPercent; }

    public float[] getHeatmapDensityStops() {
        float[] stops = new float[m_heatmapDensityStops.size()];
        for (int i = 0; i < m_heatmapDensityStops.size(); ++i) {
            stops[i] = Float.valueOf(m_heatmapDensityStops.get(i));
        }
        return stops;
    }

    public double[] getHeatmapRadii() {
        double[] heatmapRadii = new double[m_heatmapRadii.size()];
        for (int i = 0; i < m_heatmapRadii.size(); ++i) {
            heatmapRadii[i] = Double.valueOf(m_heatmapRadii.get(i));
        }
        return heatmapRadii;
    }

    public double[] getHeatmapGains() {
        double[] heatmapGains = new double[m_heatmapGains.size()];
        for (int i = 0; i < m_heatmapGains.size(); ++i) {
            heatmapGains[i] = Double.valueOf(m_heatmapGains.get(i));
        }
        return heatmapGains;
    }

    public boolean getUseApproximation() { return m_useApproximation; }

    public float getDensityBlend() { return m_densityBlend; }

    public boolean getInterpolateDensityByZoom() { return m_interpolateDensityByZoom; }

    public double getZoomMin() { return m_zoomMin; }

    public double getZoomMax() { return m_zoomMax; }

    public float getOpacity() { return m_opacity; }

    public float getIntensityBias() { return m_intensityBias; }

    public float getIntensityScale() { return m_intensityScale; }

    public float getOccludedStyleAlpha() { return m_occludedAlpha; }

    public float getOccludedStyleSaturation() { return m_occludedSaturation; }

    public float getOccludedStyleBrightness()  { return m_occludedBrightness; }

    public HeatmapOcclusionMapFeature[] getOccludedFeatures() { return m_occludedFeatures; }

    public float[] getGradientStops() { return m_gradientStops; }

    public int[] getGradientColors() { return m_gradientColors; }
}
