package com.eegeo.mapapi.positioner;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;

/**
 * Defines creation parameters for a Positioner.
 * <br>
 * <br>
 * For more information, see the Positioner documentation.
 */
public final class PositionerOptions {

    private LatLng m_position;
    private double m_elevation;
    private ElevationMode m_elevationMode = ElevationMode.HeightAboveGround;
    private String m_indoorMapId = "";
    private int m_indoorFloorId;
    /**
     * Instantiate a new set of positioner options
     */
    public PositionerOptions() {

    }

    /**
     * Sets the position for the positioner.
     *
     * @param position The new position for the positioner.
     * @return The PositionerOptions object on which the method was called, with the new position set.
     */
    public PositionerOptions position(LatLng position) {
        m_position = position;
        return this;
    }

    /**
     * Sets the elevation for the positioner. If this method is not called, PositionerOptions will be
     * initialised to create a Positioner with an elevation of 0.
     *
     * @param elevation The elevation, in meters.
     * @return The PositionerOptions object on which the method was called, with the new elevation set.
     */
    public PositionerOptions elevation(double elevation) {
        m_elevation = elevation;
        return this;
    }

    /**
     * Sets the ElevationMode for the positioner. If this method is not called, PositionerOptions will be
     * initialised to create a Positioner with ElevationMode.HeightAboveGround.
     *
     * @param elevationMode The ElevationMode used to interpret the elevation of the positioner.
     * @return The PositionerOptions object on which the method was called, with the new elevation mode set.
     */
    public PositionerOptions elevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        return this;
    }

    /**
     * Sets the indoor map properties for the positioner. If this method is not called,
     * PositionerOptions is initialised to create a positioner for display on an outdoor map.
     *
     * @param indoorMapId   The identifier of the indoor map on which the positioner should be displayed.
     * @param indoorFloorId The identifier of the indoor map floor on which the positioner should be
     *                      displayed. In the WRLD Indoor Map Format, this corresponds to the
     *                      'z_order' field of the Level object.
     * @return The PositionerOptions object on which the method was called, with the new indoor map
     * properties set.
     */
    public PositionerOptions indoor(String indoorMapId, int indoorFloorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = indoorFloorId;
        return this;
    }

    /**
     * Returns the position set for this PositionerOptions object.
     *
     * @return A LatLng object specifying the positioner's location
     */
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Returns the elevation set for this PositionerOptions object.
     *
     * @return A height, in meters.
     */
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Returns the elevation mode set for this PositionerOptions object.
     *
     * @return The ElevationMode, indicating how elevation is interpreted.
     */
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Returns the indoor map identifier for this PositionerOptions object.
     *
     * @return A string containing the indoor map identifier
     */
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Returns the indoor map floor identifier for this PositionerOptions object.
     *
     * @return The indoor map floor id.
     */
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }
}
