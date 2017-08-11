package com.eegeo.mapapi.markers;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;


/**
 * Defines creation parameters for a Marker.
 * <br>
 * <br>
 * For more information, see the Marker documentation.
 */
public final class MarkerOptions {

    private LatLng m_position;
    private double m_elevation;
    private ElevationMode m_elevationMode = ElevationMode.HeightAboveGround;
    private int m_drawOrder;
    private String m_title = "";
    private String m_styleName = "marker_default";
    private String m_userData = "";
    private String m_iconKey = "misc";
    private String m_indoorMapId = "";
    private int m_indoorFloorId;
    /**
     * Instantiate a new set of marker options
     */
    public MarkerOptions() {

    }

    /**
     * Sets the position for the marker.
     *
     * @param position
     * @return The MarkerOptions object on which the method was called, with the new position set.
     */
    public MarkerOptions position(LatLng position) {
        m_position = position;
        return this;
    }

    /**
     * Sets the elevation for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with an elevation of 0.
     *
     * @param elevation
     * @return The MarkerOptions object on which the method was called, with the new elevation set.
     */
    public MarkerOptions elevation(double elevation) {
        m_elevation = elevation;
        return this;
    }

    /**
     * Sets the ElevationMode for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with ElevationMode.HeightAboveGround.
     *
     * @param elevationMode
     * @return The MarkerOptions object on which the method was called, with the new elevation mode set.
     */
    public MarkerOptions elevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        return this;
    }

    /**
     * [Deprecated - use elevationMode(ElevationMode elevationMode).]
     * <br>
     * Sets the ElevationMode for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with ElevationMode.HeightAboveGround.
     *
     * @deprecated
     * @param markerElevationMode
     * @return The MarkerOptions object on which the method was called, with the new elevation mode set.
     */
    public MarkerOptions elevationMode(MarkerElevationMode markerElevationMode) {
        m_elevationMode = fromMarkerElevationMode(markerElevationMode);
        return this;
    }
    /**
     * Sets the title for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with no title.
     *
     * @param title
     * @return The MarkerOptions object on which the method was called, with the new title set.
     */
    public MarkerOptions labelText(String title) {
        m_title = title;
        return this;
    }

    /**
     * Set the style name for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with a default style.
     *
     * @param styleName
     * @return The MarkerOptions object on which the method was called, with the new style name set.
     */
    public MarkerOptions styleName(String styleName) {
        m_styleName = styleName;
        return this;
    }

    /**
     * Sets the user data for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with an empty string as user data.
     *
     * @param userData
     * @return The MarkerOptions object on which the method was called, with the new user data set.
     */
    public MarkerOptions userData(String userData) {
        m_userData = userData;
        return this;
    }

    /**
     * Sets the icon key for the marker. If this method is not called, MarkerOptions will be
     * initialised to create a Marker with a default icon.
     *
     * @param iconKey
     * @return The MarkerOptions object on which the method was called, with the new icon key set.
     */
    public MarkerOptions iconKey(String iconKey) {
        m_iconKey = iconKey;
        return this;
    }

    /**
     * Sets the indoor map properties for the marker. If this method is not called,
     * MarkerOptions is initialised to create a marker for display on an outdoor map.
     *
     * @param indoorMapId   the identifier of the indoor map on which the marker should be displayed
     * @param indoorFloorId the identifier of the indoor map floor on which the marker should be
     *                      displayed. In the WRLD Indoor Map Format, this corresponds to the
     *                      'z_order' field of the Level object.
     * @return The MarkerOptions object on which the method was called, with the new indoor map
     * properties set.
     */
    public MarkerOptions indoor(String indoorMapId, int indoorFloorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = indoorFloorId;
        return this;
    }

    /**
     * Sets the draw order for the marker. If this method is not called, MarkerOptions is
     * initialised to create a marker with a drawOrder of 0.
     *
     * @param drawOrder
     * @return The MarkerOptions object on which the method was called, with the draw order set
     */
    public MarkerOptions drawOrder(int drawOrder) {
        m_drawOrder = drawOrder;
        return this;
    }

    /**
     * Returns the position set for this MarkerOptions object.
     *
     * @return A LatLng object specifying the marker's location
     */
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Returns the elevation set for this MarkerOptions object.
     *
     * @return A height, in meters.
     */
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Returns the elevation mode set for this MarkerOptions object.
     *
     * @return The ElevationMode, indicating how elevation is interpreted.
     */
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Returns the title set for this MarkerOptions object.
     *
     * @return A string containing the title text.
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Returns the draw order set for this MarkerOptions object
     *
     * @return the draw order
     */
    public int getDrawOrder() {
        return m_drawOrder;
    }

    /**
     * Returns the style name set for this MarkerOptions object.
     *
     * @return A string containing the style name.
     */
    public String getStyleName() {
        return m_styleName;
    }

    /**
     * Returns the user data set for this MarkerOptions object.
     *
     * @return A string containing the user data.
     */
    public String getUserData() {
        return m_userData;
    }

    /**
     * Returns the icon key set for this MarkerOptions object.
     *
     * @return A string containing the icon key.
     */
    public String getIconKey() {
        return m_iconKey;
    }

    /**
     * Returns the indoor map identifier for this MarkerOptions object.
     *
     * @return A string containing the indoor map identifier
     */
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Returns the indoor map floor identifier for this MarkerOptions object.
     *
     * @return The indoor map floor id.
     */
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Specifies how a Marker's elevation property is interpreted
     * @deprecated in favor of mapapi.geometry.ElevationMode . To ease migration, conversion methods
     * fromMarkerElevationMode and toMarkerElevationMode have been provided.
     */
    public enum MarkerElevationMode {
        /**
         * Indicates that a Marker's elevation property is interpreted as an absolute altitude above
         * mean sea level, in meters.
         */
        HeightAboveSeaLevel,

        /**
         * Indicates that a Marker's elevation property is interpreted as a height relative to the
         * map's terrain at the marker's LatLng position, in meters
         */
        HeightAboveGround
    }

    /**
     * Provided for migration from deprecated type MarkerElevationMode to ElevationMode only
     * @deprecated
     * @return markerElevationMode as an equivalent ElevationMode
     */
    public static ElevationMode fromMarkerElevationMode(MarkerElevationMode markerElevationMode) {
        if (markerElevationMode == MarkerElevationMode.HeightAboveGround) {
            return ElevationMode.HeightAboveGround;
        }
        return ElevationMode.HeightAboveSeaLevel;
    }

    /**
     * Provided for migration from deprecated type MarkerElevationMode to ElevationMode only
     * @deprecated
     * @return elevationMode as an equivalent MarkerElevationMode
     */
    public static MarkerElevationMode toMarkerElevationMode(ElevationMode elevationMode) {
        if (elevationMode == ElevationMode.HeightAboveGround) {
            return MarkerElevationMode.HeightAboveGround;
        }
        return MarkerElevationMode.HeightAboveSeaLevel;
    }

}
