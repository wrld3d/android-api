package com.eegeo.mapapi.heatmaps;

import com.eegeo.mapapi.geometry.WeightedLatLngAlt;
import com.eegeo.mapapi.polygons.PolygonOptions;

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
    private List<WeightedLatLngAlt> m_data = new ArrayList<>();
    private double m_weightMin = 0.0;
    private double m_weightMax = 1.0;

    private int m_resolutionPixels = 512;
    private double m_radiusMinMeters = 5.0;
    private double m_radiusMaxMeters = 25.0;
    private double m_radiusBlend = 5.0;
    private double m_opacity = 0.70;
    private double m_intensityScale = 1.0;
    private float m_occludedAlpha = 0.85f;
    private float m_occludedSaturation = 0.7f;
    private float m_occludedBrightness = 0.7f;
    private int m_occludedFeatures = OCCLUSION_NONE;

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
        Collections.addAll(m_data, data);
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
        m_data.add(data);
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
        m_resolutionPixels = resolutionPixels;
        return this;
    }

    public HeatmapOptions radiusMinMeters(double radiusMinMeters) {
        m_radiusMinMeters = radiusMinMeters;
        return this;
    }

    public HeatmapOptions radiusMaxMeters(double radiusMaxMeters) {
        m_radiusMaxMeters = radiusMaxMeters;
        return this;
    }

    public HeatmapOptions radiusBlend(double radiusBlend) {
        m_radiusBlend = radiusBlend;
        return this;
    }

    public HeatmapOptions opacity(double opacity) {
        m_opacity = opacity;
        return this;
    }

    public HeatmapOptions intensityScale(double intensityScale) {
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

    public PolygonOptions getPolygonOptions() {
        return m_polygonOptions;
    }


    /**
     * Returns the points for this HeatmapOptions object.
     *
     * @return A list of LatLng objects specifying the heatmap's vertices.
     */
    public List<WeightedLatLngAlt> getData() {
        return m_data;
    }

    public double getWeightMin() { return m_weightMin; }

    public double getWeightMax() { return m_weightMax; }

    public int getTextureWidth() { return m_resolutionPixels; }

    public int getTextureHeight() { return m_resolutionPixels; }

    public double getRadiusMinMeters() { return m_radiusMinMeters; }

    public double getRadiusMaxMeters() { return m_radiusMaxMeters; }

    public double getRadiusBlend() { return m_radiusBlend; }

    public double getOpacity() { return m_opacity; }

    public double getIntensityScale() { return m_intensityScale; }

    public float getOccludedStyleAlpha() { return m_occludedAlpha; }

    public float getOccludedStyleSaturation() { return m_occludedSaturation; }

    public float getOccludedStyleBrightness()  { return m_occludedBrightness; }

    public int getOccludedFeatures() { return m_occludedFeatures; }
}
