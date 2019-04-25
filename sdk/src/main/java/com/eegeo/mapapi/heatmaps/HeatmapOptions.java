package com.eegeo.mapapi.heatmaps;

import com.eegeo.mapapi.geometry.WeightedLatLngAlt;
import com.eegeo.mapapi.polygons.PolygonOptions;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines creation parameters for a [Heatmap]({{ site.baseurl }}/docs/api/Heatmap).
 * @see Heatmap
 */
@SuppressWarnings("WeakerAccess")
public final class HeatmapOptions {

    private List<WeightedLatLngAlt> m_weightedPoints = new ArrayList<>();
    private List<Float> m_heatmapDensityStops = new ArrayList<>();
    private List<Double> m_heatmapRadii = new ArrayList<>();
    private List<Double> m_heatmapGains = new ArrayList<>();
    private float m_densityBlend = 0.0f;
    private boolean m_interpolateDensityByZoom = false;
    private double m_zoomMin = 15.0;
    private double m_zoomMax = 18.0;
    private double m_weightMin = 0.0;
    private double m_weightMax = 1.0;

    // http://colorbrewer2.org/#type=sequential&scheme=OrRd&n=5
    // with additional ramp to transparent white below 20%
    private float[] m_gradientStops = {0.f, 0.2f, 0.4f, 0.6f, 0.8f, 1.f};
    private int[] m_gradientColors = {0xffffff00, 0xfef0d9ff, 0xfdcc8aff, 0xfc8d59ff, 0xe34a33ff, 0xb30000ff};
    private float m_opacity = 1.f;
    private int m_resolutionPixels = 512;
    private float m_intensityBias = 0.0f;
    private float m_intensityScale = 1.0f;
    private PolygonOptions m_polygonOptions = new PolygonOptions();
    private float m_occludedAlpha = 0.85f;
    private float m_occludedSaturation = 0.7f;
    private float m_occludedBrightness = 0.7f;
    private HeatmapOcclusionMapFeature[] m_occludedMapFeatures = {HeatmapOcclusionMapFeature.buildings, HeatmapOcclusionMapFeature.trees};
    private float m_textureBorderPercent = 0.05f;
    private boolean m_useApproximation = true;

    /**
     * Minimum value of the resolutionPixels option
     */
    public static int RESOLUTION_PIXELS_MIN = 32;

    /**
     * Maximum value of the resolutionPixels option
     */
    public static int RESOLUTION_PIXELS_MAX = 2048;

    /**
     * Default constructor for heatmap creation parameters.
     */
    public HeatmapOptions() {

    }

    /**
     * Adds data points to the heatmap.
     * The WeightedLatLngAlt.intensity field is used as a weighting value. A weight of N is
     * equivalent to placing N points all at the same coordinate, each with a weight of 1.
     *
     * @param data The data points to add.
     * @return This object.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions add(WeightedLatLngAlt... data) {
        Collections.addAll(m_weightedPoints, data);
        return this;
    }

    /**
     * Adds a point to the end of the current data point collection.
     * The WeightedLatLngAlt.intensity field is used as a weighting value. A weight of N is
     * equivalent to placing N points all at the same coordinate, each with a weight of 1.
     *
     * @param data The point to add.
     * @return This object.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions add(WeightedLatLngAlt data) {
        m_weightedPoints.add(data);
        return this;
    }

    /**
     * Sets options to have a single densityStop entry with defined radius, and default gain.
     *
     * @param heatmapRadiusMeters
     * @return
     */
    public HeatmapOptions heatmapRadius(double heatmapRadiusMeters) {
        m_heatmapDensityStops.clear();
        m_heatmapRadii.clear();
        m_heatmapGains.clear();
        m_heatmapDensityStops.add(0.0f);
        m_heatmapRadii.add(heatmapRadiusMeters);
        m_heatmapGains.add(1.0);
        return this;
    }

    /**
     * Add a density stop to the end of the current collection of density stops.
     *
     * @param stop A stop function input value in the range [0..1].
     * @param heatmapRadiusMeters The radius of the circle drawn for each point, in meters.
     * @param heatmapGain Intensity multiplier.
     * @return This object.
     */
    public HeatmapOptions addDensityStop(float stop, double heatmapRadiusMeters, double heatmapGain) {
        m_heatmapDensityStops.add(stop);
        m_heatmapRadii.add(heatmapRadiusMeters);
        m_heatmapGains.add(heatmapGain);
        return this;
    }

    /**
     * Set the density stops collection.
     *
     * @throws InvalidParameterException if parameter arrays are empty or not of equal length.
     * @param stops Array of stop function input values in the range [0..1].
     * @param heatmapRadiiMeters Array of radii of the circles drawn for each point, in meters.
     * @param heatmapGains Array of intensity multipliers.
     * @return
     */
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

    /**
     * Defines a domain mapping from heatmap intensity value [weightMin..weightMax] to colorGradient
     * stop function input [0..1].
     *
     * @param weightMin The option value.
     * @return This object.
     */
    public HeatmapOptions weightMin(double weightMin) {
        m_weightMin = weightMin;
        return this;
    }

    /**
     * Defines a domain mapping from heatmap intensity value [weightMin..weightMax] to colorGradient
     * stop function input [0..1].
     *
     * @param weightMax The option value.
     * @return This object.
     */
    public HeatmapOptions weightMax(double weightMax) {
        m_weightMax = weightMax;
        return this;
    }



    /**
     * Controls which of the densityStops heatmap images is displayed, by linearly interpolating
     * between the two adjacent stops whose stop value is closest to densityBlend. The parameter is
     * clamped to the range [0..1].
     *
     * @param densityBlend The option value.
     * @return This object.
     */
    public HeatmapOptions densityBlend(float densityBlend) {
        m_densityBlend = Math.min(Math.max(densityBlend, 0.0f), 1.0f);
        m_interpolateDensityByZoom = false;
        return this;
    }

    /**
     * Sets the options to blend between density stops based on current map camera zoom level.
     * In this case, densityBlend is ignored. Options zoomMin and zoomMax control the extents over
     * which heatmap density will change with map zoom, linearly interpolating between them.
     *
     * @param zoomMin The camera zoom level at which the highest density stop is displayed
     *                (with stop of 1.0). Ignored if interpolateDensityByZoom is false.
     * @param zoomMax The camera zoom level at which the lowest density stop is displayed
     *                (with stop of 0.0). Ignored if interpolateDensityByZoom is false.
     * @return This object.
     */
    public HeatmapOptions interpolateDensityByZoom(double zoomMin, double zoomMax) {
        m_interpolateDensityByZoom = true;
        m_zoomMin = Math.max(zoomMin, 0.0);
        m_zoomMax = Math.max(zoomMax, 0.0);
        return this;
    }

    /**
     * Sets the color ramp applied to each pixel of the heatmap intensity image, from minimum
     * intensity weightMin at stop == 0.0 to maximum intensity weightMax at stop == 1.0.
     *
     * @throws InvalidParameterException if the input parameter arrays are not of equal length.
     * @param stops Stop function input values in the range [0..1].
     * @param colors The color value for each stop, as 32-bit ARGB color.
     * @return This object.
     */
    public HeatmapOptions gradient(float[] stops, int[] colors) {
        if (stops.length != colors.length) {
            throw new InvalidParameterException("gradient stops and colors arrays must be equal length");
        }
        m_gradientStops = stops;
        m_gradientColors = colors;
        return this;
    }

    /**
     * Sets the overall opacity of the heatmap. The parameter is clamped to the range [0..1].
     *
     * @param opacity The option value.
     * @return This object.
     */
    public HeatmapOptions opacity(float opacity) {
        m_opacity = Math.min(Math.max(opacity, 0.0f), 1.0f);
        return this;
    }

    /**
     * Defines the maximum dimension, in pixels, of the intensity image calculated for each heatmap
     * density stop. A higher value allows more detail to be visible in the heatmap, but requires
     * more computational resources.
     *
     * @param resolutionPixels The option value.
     * @return This object.
     */
    public HeatmapOptions resolutionPixels(int resolutionPixels) {
        m_resolutionPixels = Math.min(
                Math.max(resolutionPixels, HeatmapOptions.RESOLUTION_PIXELS_MIN),
                HeatmapOptions.RESOLUTION_PIXELS_MAX
        );
        return this;
    }

    /**
     * Set options for defining a clipping polygon. Also contains options for elevation and indoor map.
     * @param polygonOptions The polygon options instance.
     * @return This object.
     */
    public HeatmapOptions polygon(PolygonOptions polygonOptions) {
        this.m_polygonOptions = polygonOptions;
        return this;
    }

    /**
     * Determines the normative value of the data represented by the heatmap, corresponding to a
     * point on the colorGradient function. This is useful when displaying weighted point data that
     * diverges around some center value, for example temperatures above or below freezing point.
     * In this case, intensityBias might be set to 0.5 to correspond with the mid-point of the
     * colorGradient function. This would correspond to a normative temperature value at the
     * mid-point between weightMin and weightMax. The property is clamped to the range [0..1].
     *
     * @param intensityBias The option value.
     * @return This object.
     */
    public HeatmapOptions intensityBias(float intensityBias) {
        m_intensityBias = Math.min(Math.max(intensityBias, 0.0f), 1.0f);;
        return this;
    }

    /**
     * Sets an additional overall intensity multiplier. The scaling value is applied relative to the
     * origin (normative value) determined by intensityBias.
     *
     * @param intensityScale The option value.
     * @return This object.
     */
    public HeatmapOptions intensityScale(float intensityScale) {
        m_intensityScale = Math.max(intensityScale, 0.0f);
        return this;
    }

    /**
     * Sets the map feature types that, if occluding areas of the heatmap, will cause those areas of
     * the heatmap to be drawn with an alternate style.
     *
     * @param occludedMapFeatures The option value.
     * @return This object.
     */
    public HeatmapOptions occludedMapFeatures(HeatmapOcclusionMapFeature[] occludedMapFeatures) {
        m_occludedMapFeatures = occludedMapFeatures;
        return this;
    }

    /**
     * Sets the opacity of occluded areas of the heatmap, clamped to the range [0..1].
     *
     * @param occludedAlpha The option value.
     * @return This object.
     */
    public HeatmapOptions occludedStyleAlpha(float occludedAlpha) {
        m_occludedAlpha = Math.min(Math.max(occludedAlpha, 0.0f), 1.0f);
        return this;
    }

    /**
     * Sets the color saturation of occluded areas of the heatmap, clamped to the range [0..1],
     * where 0.0 is fully desaturated (grayscale), and 1.0 has no effect.
     *
     * @param occludedSaturation The option value.
     * @return This object.
     */
    public HeatmapOptions occludedStyleSaturation(float occludedSaturation) {
        m_occludedSaturation = Math.min(Math.max(occludedSaturation, 0.0f), 1.0f);;
        return this;
    }

    /**
     * Sets the color brightness of occluded areas of the heatmap, clamped to the range [0..1],
     * where 0.0 is fully darkened (black), and 1.0 has no effect.
     *
     * @param occludedBrightness The option value.
     * @return This object.
     */
    public HeatmapOptions occludedStyleBrightness(float occludedBrightness) {
        m_occludedBrightness = Math.min(Math.max(occludedBrightness, 0.0f), 1.0f);;
        return this;
    }

    /**
     * Sets the size of a gutter at the edges of the heatmap images, around the bounds of the data
     * points. Specified as a percentage of the image dimensions.
     *
     * @param textureBorderPercent The option value.
     * @return This object.
     */
    public HeatmapOptions textureBorder(float textureBorderPercent) {
        m_textureBorderPercent = Math.min(
                Math.max(textureBorderPercent, 0.0f),
                0.5f
        );
        return this;
    }

    /**
     * Set the useApproximation option. If false, uses a slightly more accurate but computationally
     * costly method for calculating the heatmap.
     *
     * @param useApproximation The option value.
     * @return This object.
     */
    public HeatmapOptions useApproximation(boolean useApproximation) {
        m_useApproximation = useApproximation;
        return this;
    }

    ////////

    /**
     * Returns the data points for the heatmpa.
     *
     * @return The option value.
     */
    public List<WeightedLatLngAlt> getWeightedPoints() {
        return m_weightedPoints;
    }

    /**
     * Returns the stop parameter array for the density stops.
     *
     * @return The option value.
     */
    public float[] getHeatmapDensityStops() {
        float[] stops = new float[m_heatmapDensityStops.size()];
        for (int i = 0; i < m_heatmapDensityStops.size(); ++i) {
            stops[i] = Float.valueOf(m_heatmapDensityStops.get(i));
        }
        return stops;
    }

    /**
     * Returns the circle radius array for the density stops.
     *
     * @return The option value.
     */
    public double[] getHeatmapRadii() {
        double[] heatmapRadii = new double[m_heatmapRadii.size()];
        for (int i = 0; i < m_heatmapRadii.size(); ++i) {
            heatmapRadii[i] = Double.valueOf(m_heatmapRadii.get(i));
        }
        return heatmapRadii;
    }

    /**
     * Returns the gains array for the density stops.
     *
     * @return The option value.
     */
    public double[] getHeatmapGains() {
        double[] heatmapGains = new double[m_heatmapGains.size()];
        for (int i = 0; i < m_heatmapGains.size(); ++i) {
            heatmapGains[i] = Double.valueOf(m_heatmapGains.get(i));
        }
        return heatmapGains;
    }

    /**
     * Returns the densityBlend option.
     *
     * @return The option value.
     */
    public float getDensityBlend() { return m_densityBlend; }

    /**
     * Returns the interpolateDensityByZoom option.
     *
     * @return The option value.
     */
    public boolean getInterpolateDensityByZoom() { return m_interpolateDensityByZoom; }

    /**
     * Returns the zoomMin option.
     *
     * @return The option value.
     */
    public double getZoomMin() { return m_zoomMin; }

    /**
     * Returns the zoomMax option.
     *
     * @return The option value.
     */
    public double getZoomMax() { return m_zoomMax; }

    /**
     * Returns the weightMin option.
     *
     * @return The option value.
     */
    public double getWeightMin() { return m_weightMin; }

    /**
     * Returns the weightMax option.
     *
     * @return The option value.
     */
    public double getWeightMax() { return m_weightMax; }

    /**
     * Returns the gradientStops option.
     *
     * @return The option value.
     */
    public float[] getGradientStops() { return m_gradientStops; }

    /**
     * Returns the gradientColors option.
     *
     * @return The option value.
     */
    public int[] getGradientColors() { return m_gradientColors; }

    /**
     * Returns the opacity option.
     *
     * @return The option value.
     */
    public float getOpacity() { return m_opacity; }

    /**
     * Returns the resolutionPixels option.
     * @return The option value.
     */
    public int getResolutionPixels() { return m_resolutionPixels; }

    /**
     * Returns the intensityBias option.
     *
     * @return The option value.
     */
    public float getIntensityBias() { return m_intensityBias; }

    /**
     * Returns the intensityScale option.
     *
     * @return The option value.
     */
    public float getIntensityScale() { return m_intensityScale; }

    /**
     * Returns the options defining a clipping polygon for the heatmap, and also indoor map and
     * elevation options.
     * @return The option value.
     */
    public PolygonOptions getPolygonOptions() {
        return m_polygonOptions;
    }

    /**
     * Returns the occludedAlpha option.
     *
     * @return The option value.
     */
    public float getOccludedStyleAlpha() { return m_occludedAlpha; }

    /**
     * Returns the occludedSaturation option.
     *
     * @return The option value.
     */
    public float getOccludedStyleSaturation() { return m_occludedSaturation; }

    /**
     * Returns the occludedBrightness option.
     *
     * @return The option value.
     */
    public float getOccludedStyleBrightness()  { return m_occludedBrightness; }

    /**
     * Returns the occludedMapFeatures option.
     *
     * @return The option value.
     */
    public HeatmapOcclusionMapFeature[] getOccludedMapFeatures() { return m_occludedMapFeatures; }

    /**
     * Returns the textureBorderPercent option.
     *
     * @return The option value.
     */
    public float getTextureBorderPercent() { return m_textureBorderPercent; }

    /**
     * Returns the useApproximation option.
     *
     * @return The option value.
     */
    public boolean getUseApproximation() { return m_useApproximation; }

}
