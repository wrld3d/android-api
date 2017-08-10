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
    private float m_width = 10;
    private int m_colorARGB = 0xff000000;

    /**
     * Default constructor for polyline creation parameters.
     */
    public PolylineOptions() {

    }

    /**
     * Adds vertices to the end of the polyline.
     *
     * @param points
     * @return The PolylineOptions object on which the method was called, with the new points added.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions add(LatLng... points) {
        Collections.addAll(m_points, points);
        return this;
    }

    /**
     * Adds a vertex to the end of the polyline.
     *
     * @param point
     * @return The PolylineOptions object on which the method was called, with the new point added.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions add(LatLng point) {
        m_points.add(point);
        return this;
    }

    /**
     * Sets the elevation for the polyline. If this method is not called, PolylineOptions will be
     * initialised to create a Polyline with an elevation of 0.
     *
     * @param elevation
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
     * @param ElevationMode
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
     * @param width
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
     * @param color
     * @return The PolylineOptions object on which the method was called, with the new color set.
     */
    @SuppressWarnings("JavaDoc")
    public PolylineOptions color(int color) {
        m_colorARGB = color;
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

}
