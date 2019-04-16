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
    public static final int OCCLUSION_NONE = 0x0;
    public static final int OCCLUSION_GROUND = 0x1;
    public static final int OCCLUSION_BUILDINGS = 0x2;
    public static final int OCCLUSION_TREES = 0x4;
    public static final int OCCLUSION_TRANSPORT = 0x8;


    private PolygonOptions m_polygonOptions = new PolygonOptions();
    private List<WeightedLatLngAlt> m_weightedPoints = new ArrayList<>();
    private double m_weightMin = 0.0;
    private double m_weightMax = 1.0;

    private int m_resolutionPixels = 512;
    private float m_textureBorderPercent = 0.05f;
    private List<Double> m_heatmapRadii = new ArrayList<>();
    private List<Float> m_heatmapRadiiStartParams = new ArrayList<>();
    private boolean m_useApproximation = true;
    private float m_radiusBlend = 0.0f;
    private float m_opacity = 1.f;
    private float m_intensityBias = 0.0f;
    private float m_intensityScale = 1.0f;
    private float m_occludedAlpha = 0.85f;
    private float m_occludedSaturation = 0.7f;
    private float m_occludedBrightness = 0.7f;
    private int m_occludedFeatures = OCCLUSION_NONE;
    // http://colorbrewer2.org/#type=sequential&scheme=OrRd&n=5
    // with additional ramp to transparent white below 20%
    private int[] m_gradientColors = {0xffffff00, 0xfef0d9ff, 0xfdcc8aff, 0xfc8d59ff, 0xe34a33ff, 0xb30000ff};
    private float[] m_gradientStartParams = {0.f, 0.2f, 0.4f, 0.6f, 0.8f, 1.f};

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
        m_heatmapRadii.clear();
        m_heatmapRadiiStartParams.clear();
        m_heatmapRadii.add(heatmapRadiusMeters);
        m_heatmapRadiiStartParams.add(0.0f);
        return this;
    }

    public HeatmapOptions addHeatmapRadius(double heatmapRadiusMeters, float startParam) {
        m_heatmapRadii.add(heatmapRadiusMeters);
        m_heatmapRadiiStartParams.add(startParam);
        return this;
    }

    public HeatmapOptions setHeatmapRadii(double[] heatmapRadiiMeters, float[] startParams) {
        m_heatmapRadii.clear();
        m_heatmapRadiiStartParams.clear();

        if (heatmapRadiiMeters.length == 0) {
            throw new InvalidParameterException("heatmapRadiiMeters must not be empty");
        }

        if (heatmapRadiiMeters.length != startParams.length) {
            throw new InvalidParameterException("heatmapRadiiMeters and startParams must be equal length");
        }

        for (int i = 0; i < heatmapRadiiMeters.length; ++i) {
            m_heatmapRadii.add(heatmapRadiiMeters[i]);
            m_heatmapRadiiStartParams.add(startParams[i]);
        }
        return this;
    }

    public HeatmapOptions useApproximation(boolean enableApproximation) {
        m_useApproximation = enableApproximation;
        return this;
    }

    // todo_heatmap rename weightMin / Max -> intensityMin / Max
    // or density instead of intensity?
    public HeatmapOptions weightMin(double weightMin) {
        m_weightMin = weightMin;
        return this;
    }

    public HeatmapOptions weightMax(double weightMax) {
        m_weightMax = weightMax;
        return this;
    }

    public HeatmapOptions resolution(int resolutionPixels) {
        m_resolutionPixels = resolutionPixels;
        return this;
    }

    public HeatmapOptions textureBorder(float textureBorderPercent) {
        m_textureBorderPercent = textureBorderPercent;
        return this;
    }


    public HeatmapOptions radiusBlend(float radiusBlend) {
        m_radiusBlend = radiusBlend;
        return this;
    }

    public HeatmapOptions opacity(float opacity) {
        m_opacity = opacity;
        return this;
    }

    public HeatmapOptions intensityBias(float intensityBias) {
        m_intensityBias = intensityBias;
        return this;
    }

    public HeatmapOptions intensityScale(float intensityScale) {
        m_intensityScale = intensityScale;
        return this;
    }

    public HeatmapOptions occludedFeatures(int occludedFeatures) {
        m_occludedFeatures = occludedFeatures;
        return this;
    }

    public HeatmapOptions occludedStyleAlpha(float occludedAlpha) {
        m_occludedAlpha = occludedAlpha;
        return this;
    }

    public HeatmapOptions occludedStyleSaturation(float occludedSaturation) {
        m_occludedSaturation = occludedSaturation;
        return this;
    }

    public HeatmapOptions occludedStyleBrightness(float occludedBrightness) {
        m_occludedBrightness = occludedBrightness;
        return this;
    }

    public HeatmapOptions gradient(int[] colors, float[] startParams) {
        m_gradientColors = colors;
        m_gradientStartParams = startParams;
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

    public double[] getHeatmapRadii() {
        double[] heatmapRadii = new double[m_heatmapRadii.size()];
        for (int i = 0; i < m_heatmapRadii.size(); ++i) {
            heatmapRadii[i] = Double.valueOf(m_heatmapRadii.get(i));
        }
        return heatmapRadii;
    }

    public float[] getHeatmapRadiiStartParams() {
        float[] startParams = new float[m_heatmapRadiiStartParams.size()];
        for (int i = 0; i < m_heatmapRadiiStartParams.size(); ++i) {
            startParams[i] = Float.valueOf(m_heatmapRadiiStartParams.get(i));
        }
        return startParams;
    }

    public boolean getUseApproximation() { return m_useApproximation; }

    public float getRadiusBlend() { return m_radiusBlend; }

    public float getOpacity() { return m_opacity; }

    public float getIntensityBias() { return m_intensityBias; }

    public float getIntensityScale() { return m_intensityScale; }

    public float getOccludedStyleAlpha() { return m_occludedAlpha; }

    public float getOccludedStyleSaturation() { return m_occludedSaturation; }

    public float getOccludedStyleBrightness()  { return m_occludedBrightness; }

    public int getOccludedFeatures() { return m_occludedFeatures; }

    public float[] getGradientStartParams() { return m_gradientStartParams; }

    public int[] getGradientColors() { return m_gradientColors; }
}
