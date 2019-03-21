package com.eegeo.mapapi.heatmaps;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Defines creation parameters for a Heatmap.
 */
@SuppressWarnings("WeakerAccess")
public final class HeatmapOptions {

    private double m_elevation;
    private ElevationMode m_elevationMode = ElevationMode.HeightAboveGround;
    private String m_indoorMapId = "";
    private int m_indoorFloorId;
    private List<LatLng> m_points = new ArrayList<>();
    private List<List<LatLng>> m_holes = new ArrayList<>();
    private int m_fillColorARGB = 0xff000000;

    /**
     * Default constructor for heatmap creation parameters.
     */
    public HeatmapOptions() {

    }

    /**
     * Adds vertices to the end of the heatmap.
     *
     * @param points The points to add.
     * @return The HeatmapOptions object on which the method was called, with the new points added.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions add(LatLng... points) {
        Collections.addAll(m_points, points);
        return this;
    }

    /**
     * Adds a vertex to the end of the heatmap.
     *
     * @param point The point to add.
     * @return The HeatmapOptions object on which the method was called, with the new point added.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions add(LatLng point) {
        m_points.add(point);
        return this;
    }

    /**
     * Adds a hole to the heatmap being built.
     *
     * @param points The points which form the hole.
     * @return The HeatmapOptions object on which the method was called, with the new hole added.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions addHole(Iterable<LatLng> points) {
        List<LatLng> pointsList = new ArrayList<>();
        for (LatLng point : points) {
            pointsList.add(point);
        }
        m_holes.add(pointsList);
        return this;
    }

    /**
     * Sets the elevation for the heatmap. If this method is not called, HeatmapOptions will be
     * initialised to create a Heatmap with an elevation of 0.
     *
     * @param elevation The elevation, in meters.
     * @return The HeatmapOptions object on which the method was called, with the new elevation set.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions elevation(double elevation) {
        m_elevation = elevation;
        return this;
    }

    /**
     * Sets the ElevationMode for the heatmap. If this method is not called, HeatmapOptions will be
     * initialised to create a Heatmap with ElevationMode.HeightAboveGround.
     *
     * @param ElevationMode The ElevationMode used to interpret the elevation of the heatmap.
     * @return The HeatmapOptions object on which the method was called, with the new elevation mode set.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions elevationMode(ElevationMode ElevationMode) {
        m_elevationMode = ElevationMode;
        return this;
    }

    /**
     * Sets the indoor map properties for the heatmap. If this method is not called,
     * HeatmapOptions is initialised to create a heatmap for display on an outdoor map.
     *
     * @param indoorMapId   The identifier of the indoor map on which the heatmap should be displayed.
     * @param indoorFloorId The identifier of the indoor map floor on which the heatmap should be
     *                      displayed. In the WRLD Indoor Map Format, this corresponds to the
     *                      'z_order' field of the Level object.
     * @return The HeatmapOptions object on which the method was called, with the new indoor map
     * properties set.
     */
    public HeatmapOptions indoor(String indoorMapId, int indoorFloorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = indoorFloorId;
        return this;
    }

    /**
     * Sets the fill color of the heatmap as a 32-bit ARGB color. The default value is opaque black (0xff000000).
     *
     * @param fillColor The fill color to use.
     * @return The HeatmapOptions object on which the method was called, with the new color set.
     */
    @SuppressWarnings("JavaDoc")
    public HeatmapOptions fillColor(int fillColor) {
        m_fillColorARGB = fillColor;
        return this;
    }

    /**
     * Returns the elevation set for this HeatmapOptions object.
     *
     * @return A height, in meters.
     */
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Returns the elevation mode set for this HeatmapOptions object.
     *
     * @return The ElevationMode, indicating how elevation is interpreted.
     */
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Returns the indoor map identifier for this HeatmapOptions object.
     *
     * @return A string containing the indoor map identifier.
     */
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Returns the indoor map floor identifier for this HeatmapOptions object.
     *
     * @return The indoor map floor id.
     */
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Returns the points for this HeatmapOptions object.
     *
     * @return A list of LatLng objects specifying the heatmap's vertices.
     */
    public List<LatLng> getPoints() {
        return m_points;
    }

    /**
     * Returns the holes for this HeatmapOptions object.
     *
     * @return A list of lists of LatLng objects specifying the vertices for each of the Heatmap's holes.
     */
    public List<List<LatLng>> getHoles() { return m_holes; }

    /**
     * Returns the fill color set for this HeatmapOptions object.
     *
     * @return The fill color as a 32-bit ARGB color.
     */
    public int getFillColor() { return m_fillColorARGB; }

}
