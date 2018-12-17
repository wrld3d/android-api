package com.eegeo.mapapi.props;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;


/**
 * Defines creation parameters for a Prop.
 */
@SuppressWarnings("WeakerAccess")
public final class PropOptions {

    private double m_elevation;
    private ElevationMode m_elevationMode = ElevationMode.HeightAboveGround;
    private String m_indoorMapId = "";
    private int m_indoorFloorId;
    private LatLng m_position;
    private double m_headingDegrees;
    private String m_geometryId = "";
    private String m_name = "";

    /**
     * Default constructor for prop creation parameters.
     */
    public PropOptions() {

    }

    /**
     * Set the position where prop will be drawn.
     *
     * @param position The position for the prop
     * @return The PropOptions object on which the method was called, with the new position set.
     */
    @SuppressWarnings("JavaDoc")
    public PropOptions position(LatLng position) {
        m_position = position;
        return this;
    }

    /**
     * Sets the elevation for the prop. If this method is not called, PropOptions will be
     * initialised to create a Prop with an elevation of 0.
     *
     * @param elevation The elevation, in meters.
     * @return The PropOptions object on which the method was called, with the new elevation set.
     */
    @SuppressWarnings("JavaDoc")
    public PropOptions elevation(double elevation) {
        m_elevation = elevation;
        return this;
    }

    /**
     * Sets the ElevationMode for the prop. If this method is not called, PropOptions will be
     * initialised to create a Prop with ElevationMode.HeightAboveGround.
     *
     * @param elevationMode The ElevationMode used to interpret the elevation of the prop.
     * @return The PropOptions object on which the method was called, with the new elevation mode set.
     */
    @SuppressWarnings("JavaDoc")
    public PropOptions elevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        return this;
    }

    /**
     * Set the heading of the prop.  If this method is not called, PropOptions will be
     * initialised to create a Prop with a heading of 0 degrees (pointing North).
     * @param headingDegrees The heading, in degrees, clockwise, with 0 degrees pointing North.
     * @return The PropOptions object on which the method was called, with the new heading set.
     */
    public PropOptions headingDegrees(double headingDegrees) {
        m_headingDegrees = headingDegrees;
        return this;
    }

    /**
     * Set the id of the geometry to be rendered in the location specified by the prop.  If this
     * method is not called, PropOptions will be initialised to create a Prop with an empty
     * geometry Id, which will be invisible.
     * @param geometryId the name of the geometry to be rendered.  Available geometry is currently
     *                   curated by WRLD, please get in touch via support@wrld3d.com to discuss
     *                   additions.
     * @return The PropOptions object on which the method was called, with the new heading set.
     */
    public PropOptions geometryId(String geometryId) {
        m_geometryId = geometryId;
        return this;
    }

    /**
     * Sets the indoor map properties for the prop. At present, the prop will not display if this
     * method is not called, although functionality may be expanded in future to include outdoor
     * props.
     *
     * @param indoorMapId   The identifier of the indoor map on which the prop should be displayed.
     * @param indoorFloorId The identifier of the indoor map floor on which the prop should be
     *                      displayed. In the WRLD Indoor Map Format, this corresponds to the
     *                      'z_order' field of the Level object.
     * @return The PropOptions object on which the method was called, with the new indoor map
     * properties set.
     */
    public PropOptions indoor(String indoorMapId, int indoorFloorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = indoorFloorId;
        return this;
    }

    /**
     * Sets the name to be assigned to the prop created with these parameters, this should be unique.
     *
     * @param name the name to be assigned to the prop
     * @return The PropOptions object on which the method was called, with the new name set.
     */
    public PropOptions name(String name) {
        m_name = name;
        return this;
    }

    /**
     * Returns the elevation set for this PropOptions object.
     *
     * @return A height, in meters.
     */
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Returns the elevation mode set for this PropOptions object.
     *
     * @return The ElevationMode, indicating how elevation is interpreted.
     */
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Returns the indoor map identifier for this PropOptions object.
     *
     * @return A string containing the indoor map identifier.
     */
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Returns the indoor map floor identifier for this PropOptions object.
     *
     * @return The indoor map floor id.
     */
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Returns the name which the created prop will be assigned, this should be unique.
     *
     * @return The name that will be assigned to the created prop.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the position at which the prop will be displayed.
     *
     * @return A LatLng object representing the prop's position.
     */
    public LatLng getPosition() { return m_position; }

    /**
     * Returns The heading indicating the direction in which the prop will face, in degrees,
     * clockwise from North (0 degrees).
     *
     * @return The heading in degrees.
     */
    public double getHeadingDegrees() { return m_headingDegrees; }

    /**
     * Returns the geometry identifier for this PropOptions object.
     *
     * @return A string containing id of the geometry that will be displayed for this prop.
     */
    public String getGeometryId() {
        return m_geometryId;
    }
}
