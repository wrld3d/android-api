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

    private PolygonOptions m_polygonOptions = new PolygonOptions();
    private List<WeightedLatLngAlt> m_data = new ArrayList<>();

    private int m_resolutionPixels = 512;
    private double m_blurRadiusMeters = 10.0;
    private double m_opacity = 0.70;

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


    public HeatmapOptions resolution(int resolutionPixels) {
        m_resolutionPixels = resolutionPixels;
        return this;
    }

    public HeatmapOptions blurRadiusMeters(double blurRadiusMeters) {
        m_blurRadiusMeters = blurRadiusMeters;
        return this;
    }

    public HeatmapOptions opacity(double opacity) {
        m_opacity = opacity;
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

    public int getTextureWidth() { return m_resolutionPixels; }

    public int getTextureHeight() { return m_resolutionPixels; }

    public double getBlurRadiusMeters() { return m_blurRadiusMeters; }

    public double getOpacity() { return m_opacity; }


}
