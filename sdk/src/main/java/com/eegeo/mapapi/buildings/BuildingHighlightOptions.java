package com.eegeo.mapapi.buildings;

import android.graphics.Point;

import com.eegeo.mapapi.geometry.LatLng;


/**
 * Options used to construct a BuildingHighlight object.
 */
@SuppressWarnings("WeakerAccess")
public final class BuildingHighlightOptions {

    /**
     * @eegeo.internal
     */
    enum SelectionMode {
        SelectAtLocation,
        SelectAtScreenPoint
    }

    private LatLng m_selectionLocation = new LatLng(0.0, 0.0);
    private Point m_selectionScreenPoint = new Point(0,0);
    private int m_colorARGB = 0xff000000;
    private SelectionMode m_selectionMode = SelectionMode.SelectAtLocation;
    private boolean m_shouldCreateView = true;
    private OnBuildingInformationReceivedListener m_onBuildingInformationReceivedListener = null;

    /**
     * Default constructor for building highlight creation parameters.
     */
    public BuildingHighlightOptions() {

    }

    /**
     * Sets options to attempt to highlight any building present at the given latLng location.
     *
     * @param location The location
     * @return The BuildingHighlightOptions object on which the method was called
     */
    @SuppressWarnings("JavaDoc")
    public BuildingHighlightOptions highlightBuildingAtLocation(LatLng location) {
        m_selectionLocation = location;
        m_selectionMode = SelectionMode.SelectAtLocation;
        return this;
    }

    /**
     * Sets options to attempt to highlight any building present at the given screen point for the
     * current map view.
     *
     * @param screenPoint The screen-space point
     * @return The BuildingHighlightOptions object on which the method was called
     */
    @SuppressWarnings("JavaDoc")
    public BuildingHighlightOptions highlightBuildingAtScreenPoint(Point screenPoint) {
        m_selectionScreenPoint = screenPoint;
        m_selectionMode = SelectionMode.SelectAtScreenPoint;
        return this;
    }


    /**
     * Sets the color of the building highlight as a 32-bit ARGB color. The default value is opaque black (0xff000000).
     *
     * @param color The color to use.
     * @return The BuildingHighlightOptions object on which the method was called, with the new color set.
     */
    @SuppressWarnings("JavaDoc")
    public BuildingHighlightOptions color(int color) {
        m_colorARGB = color;
        return this;
    }

    /**
     * Sets options such that, if a BuildingHighlight object is created with these options and added
     * to a map, it will not result in any visual highlight overlay being displayed. In this case,
     * the BuildingHighlight object is used only for the purpose of retrieving BuildingInformation.
     *
     * @return The BuildingHighlightOptions object on which the method was called, with the option set.
     */
    public BuildingHighlightOptions informationOnly() {
        m_shouldCreateView = false;
        return this;
    }

    /**
     * BuildingInformation for a BuildingHighlight that is created and added to the map is fetched
     * asynchronously. This method sets a listener object to obtain notification when
     * BuildingInformation for a BuildingHighlight created with these options is received.
     *
     * @return The BuildingHighlightOptions object on which the method was called, with the option set.
     */
     public BuildingHighlightOptions buildingInformationReceivedListener(OnBuildingInformationReceivedListener listener) {
        m_onBuildingInformationReceivedListener = listener;
        return this;
    }

    /**
     * @eegeo.internal
     */
    public SelectionMode getSelectionMode() {
        return m_selectionMode;
    }

    /**
     * @eegeo.internal
     */
    public LatLng getSelectionLocation() {
        return m_selectionLocation;
    }

    /**
     * @eegeo.internal
     */
    public Point getSelectionScreenPoint() {
        return m_selectionScreenPoint;
    }

    /**
     * @eegeo.internal
     */
    public int getColor() {
        return m_colorARGB;
    }

    /**
     * @eegeo.internal
     */
    public boolean getShouldCreateView() { return m_shouldCreateView; }

    /**
     * @eegeo.internal
     */
    public OnBuildingInformationReceivedListener getOnBuildingInformationReceivedListener() { return m_onBuildingInformationReceivedListener; }

}
