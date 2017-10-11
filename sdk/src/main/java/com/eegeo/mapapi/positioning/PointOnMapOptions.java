package com.eegeo.mapapi.positioning;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;


/**
 * Defines creation parameters for a PointOnMap.
 * <br>
 * <br>
 * For more information, see the PointOnMap documentation.
 */
public final class PointOnMapOptions {

    private LatLng m_position;
    private double m_elevation;
    private ElevationMode m_elevationMode = ElevationMode.HeightAboveGround;
    private String m_userData = "";
    private String m_indoorMapId = "";
    private int m_indoorFloorId;
    /**
     * Instantiate a new set of point on map options
     */
    public PointOnMapOptions() {

    }

    /**
     * Sets the position for the point on map.
     *
     * @param position The new position for the point on map.
     * @return The PointOnMapOptions object on which the method was called, with the new position set.
     */
    public PointOnMapOptions position(LatLng position) {
        m_position = position;
        return this;
    }

    /**
     * Sets the elevation for the point on map. If this method is not called, PointOnMapOptions will be
     * initialised to create a PointOnMap with an elevation of 0.
     *
     * @param elevation The elevation, in meters.
     * @return The PointOnMapOptions object on which the method was called, with the new elevation set.
     */
    public PointOnMapOptions elevation(double elevation) {
        m_elevation = elevation;
        return this;
    }

    /**
     * Sets the ElevationMode for the point on map. If this method is not called, PointOnMapOptions will be
     * initialised to create a PointOnMap with ElevationMode.HeightAboveGround.
     *
     * @param elevationMode The ElevationMode used to interpret the elevation of the point on map.
     * @return The PointOnMapOptions object on which the method was called, with the new elevation mode set.
     */
    public PointOnMapOptions elevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        return this;
    }

    /**
     * [Deprecated - use elevationMode(ElevationMode elevationMode).]
     * <br>
     * Sets the ElevationMode for the point on map. If this method is not called, PointOnMapOptions will be
     * initialised to create a PointOnMap with ElevationMode.HeightAboveGround.
     *
     * @deprecated
     * @param pointOnMapElevationMode The PointOnMapOptions.PointOnMapElevationMode used to interpret the elevation of the point on map.
     * @return The PointOnMapOptions object on which the method was called, with the new elevation mode set.
     */
    public PointOnMapOptions elevationMode(PointOnMapElevationMode pointOnMapElevationMode) {
        m_elevationMode = fromPointOnMapElevationMode(pointOnMapElevationMode);
        return this;
    }

    /**
     * Sets the user data for the point on map. If this method is not called, PointOnMapOptions will be
     * initialised to create a PointOnMap with an empty string as user data.
     *
     * @param userData The user data for the point on map.
     * @return The PointOnMapOptions object on which the method was called, with the new user data set.
     */
    public PointOnMapOptions userData(String userData) {
        m_userData = userData;
        return this;
    }

    /**
     * Sets the indoor map properties for the point on map. If this method is not called,
     * PointOnMapOptions is initialised to create a point on map for display on an outdoor map.
     *
     * @param indoorMapId   The identifier of the indoor map on which the point on map should be displayed.
     * @param indoorFloorId The identifier of the indoor map floor on which the point on map should be
     *                      displayed. In the WRLD Indoor Map Format, this corresponds to the
     *                      'z_order' field of the Level object.
     * @return The PointOnMapOptions object on which the method was called, with the new indoor map
     * properties set.
     */
    public PointOnMapOptions indoor(String indoorMapId, int indoorFloorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = indoorFloorId;
        return this;
    }

    /**
     * Returns the position set for this PointOnMapOptions object.
     *
     * @return A LatLng object specifying the point on map's location
     */
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Returns the elevation set for this PointOnMapOptions object.
     *
     * @return A height, in meters.
     */
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Returns the elevation mode set for this PointOnMapOptions object.
     *
     * @return The ElevationMode, indicating how elevation is interpreted.
     */
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Returns the user data set for this PointOnMapOptions object.
     *
     * @return A string containing the user data.
     */
    public String getUserData() {
        return m_userData;
    }

    /**
     * Returns the indoor map identifier for this PointOnMapOptions object.
     *
     * @return A string containing the indoor map identifier
     */
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Returns the indoor map floor identifier for this PointOnMapOptions object.
     *
     * @return The indoor map floor id.
     */
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Specifies how a PointOnMap's elevation property is interpreted
     * @deprecated in favor of mapapi.geometry.ElevationMode . To ease migration, conversion methods
     * fromPointOnMapElevationMode and toPointOnMapElevationMode have been provided.
     */
    public enum PointOnMapElevationMode {
        /**
         * Indicates that a PointOnMap's elevation property is interpreted as an absolute altitude above
         * mean sea level, in meters.
         */
        HeightAboveSeaLevel,

        /**
         * Indicates that a PointOnMap's elevation property is interpreted as a height relative to the
         * map's terrain at the point on map's LatLng position, in meters
         */
        HeightAboveGround
    }

    /**
     * Provided for migration from deprecated type PointOnMapElevationMode to ElevationMode only
     * @deprecated
     * @return pointOnMapElevationMode as an equivalent ElevationMode
     */
    public static ElevationMode fromPointOnMapElevationMode(PointOnMapElevationMode pointOnMapElevationMode) {
        if (pointOnMapElevationMode == PointOnMapElevationMode.HeightAboveGround) {
            return ElevationMode.HeightAboveGround;
        }
        return ElevationMode.HeightAboveSeaLevel;
    }

    /**
     * Provided for migration from deprecated type PointOnMapElevationMode to ElevationMode only
     * @deprecated
     * @return elevationMode as an equivalent PointOnMapElevationMode
     */
    public static PointOnMapElevationMode toPointOnMapElevationMode(ElevationMode elevationMode) {
        if (elevationMode == ElevationMode.HeightAboveGround) {
            return PointOnMapElevationMode.HeightAboveGround;
        }
        return PointOnMapElevationMode.HeightAboveSeaLevel;
    }

}
