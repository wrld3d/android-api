package com.eegeo.mapapi.polylines;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Defines creation parameters for a Polyline.
 */
@SuppressWarnings("WeakerAccess")
public final class PolylineOptions {

    private double m_elevation;
    private ElevationMode m_elevationMode = ElevationMode.HeightAboveGround;
    private String m_indoorMapId = "";
    private int m_indoorFloorId;
    private List<LatLng> m_points = new ArrayList<>();
    private List<Double> m_perPointElevations = new ArrayList<>();
    private float m_width = 10.f;
    private int m_colorARGB = 0xff000000;
    private float m_miterLimit = 10.f;

    /**
     * Default constructor for polyline creation parameters.
     */
    public PolylineOptions() {

    }

    /**
     * Adds vertices to the end of the polyline.
     *
     * @param points The points to add.
     * @return The PolylineOptions object on which the method was called, with the new points added.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions add(LatLng... points) {
        Collections.addAll(m_points, points);
        for (int i=0; i<points.length; ++i) {
            m_perPointElevations.add(0.0);
        }
        return this;
    }

    /**
     * Adds a vertex to the end of the polyline.
     *
     * @param point The point to add.
     * @return The PolylineOptions object on which the method was called, with the new point added.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions add(LatLng point) {
        m_points.add(point);
        m_perPointElevations.add(0.0);
        return this;
    }

    public PolylineOptions add(LatLng point, double heightOffset) {
        m_points.add(point);
        m_perPointElevations.add(heightOffset);
        return this;
    }

    /**
     * Sets the elevation for the polyline. If this method is not called, PolylineOptions will be
     * initialised to create a Polyline with an elevation of 0.
     *
     * @param elevation The elevation, in meters.
     * @return The PolylineOptions object on which the method was called, with the new elevation set.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions elevation(double elevation) {
        m_elevation = elevation;
        return this;
    }

    /**
     * Sets the ElevationMode for the polyline. If this method is not called, PolylineOptions will be
     * initialised to create a Polyline with ElevationMode.HeightAboveGround.
     *
     * @param ElevationMode The ElevationMode used to interpret the elevation of the polyline.
     * @return The PolylineOptions object on which the method was called, with the new elevation mode set.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions elevationMode(ElevationMode ElevationMode) {
        m_elevationMode = ElevationMode;
        return this;
    }

    /**
     * Sets the indoor map properties for the polyline. If this method is not called,
     * PolylineOptions is initialised to create a polyline for display on an outdoor map.
     *
     * @param indoorMapId   the identifier of the indoor map on which the polyline should be displayed
     * @param indoorFloorId the identifier of the indoor map floor on which the polyline should be
     *                      displayed. In the WRLD Indoor Map Format, this corresponds to the
     *                      'z_order' field of the Level object.
     * @return The PolylineOptions object on which the method was called, with the new indoor map
     * properties set.
     */
    public PolylineOptions indoor(String indoorMapId, int indoorFloorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = indoorFloorId;
        return this;
    }

    /**
     * Sets the width of the polyline in screen pixels.
     *
     * @param width The width in screen pixels.
     * @return The PolylineOptions object on which the method was called, with the new width set.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions width(float width) {
        m_width = width;
        return this;
    }

    /**
     * Sets the color of the polyline as a 32-bit ARGB color. The default value is opaque black (0xff000000).
     *
     * @param color The color to use.
     * @return The PolylineOptions object on which the method was called, with the new color set.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions color(int color) {
        m_colorARGB = color;
        return this;
    }

    /**
     * Sets the miter limit of the polyline, the maximum allowed ratio between the length of a miter
     * diagonal at a join, and the line width.
     *
     * @param miterLimit The miter limit.
     * @return The PolylineOptions object on which the method was called, with the new miter limit set.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions miterLimit(float miterLimit) {
        m_miterLimit = miterLimit;
        return this;
    }

    /**
     * Returns the elevation set for this PolylineOptions object.
     *
     * @return A height, in meters.
     */
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Returns the elevation mode set for this PolylineOptions object.
     *
     * @return The ElevationMode, indicating how elevation is interpreted.
     */
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Returns the indoor map identifier for this PolylineOptions object.
     *
     * @return A string containing the indoor map identifier.
     */
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Returns the indoor map floor identifier for this PolylineOptions object.
     *
     * @return The indoor map floor id.
     */
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Returns the points for this PolylineOptions object.
     *
     * @return A list of LatLng objects specifying the polyline's vertices.
     */
    public List<LatLng> getPoints() {
        return m_points;
    }

    public List<Double> getPerPointElevations() {
        return m_perPointElevations;
    }

    /**
     * Returns the line width set for this PolylineOptions object. The default value is 10.
     *
     * @return The line width.
     */
    public float getWidth() {
        return m_width;
    }

    /**
     * Returns the color set for this PolylineOptions object.
     *
     * @return The line color as a 32-bit ARGB color.
     */
    public int getColor() {
        return m_colorARGB;
    }

    /**
     * Returns the line miter limit set for this PolylineOptions object. The default value is 10,
     * which would result in a polyline created from these options clamping the length of a join
     * diagonal for join angles less than approximately 11 degrees.
     *
     * @return The miter limit ratio used for miter joins.
     */
    public float getMiterLimit() {
        return m_miterLimit;
    }

}
