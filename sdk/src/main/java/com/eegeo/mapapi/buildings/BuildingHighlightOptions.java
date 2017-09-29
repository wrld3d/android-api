package com.eegeo.mapapi.buildings;

import android.graphics.Point;

import com.eegeo.mapapi.geometry.LatLng;


/**
 * Defines creation parameters for a BuildingHighlight.
 */
@SuppressWarnings("WeakerAccess")
public final class BuildingHighlightOptions {

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
     * Set options to create a highlight for the building closest to a LatLong location
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
     * Set options to create a highlight for the building at a screen point
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

    public BuildingHighlightOptions informationOnly() {
        m_shouldCreateView = false;
        return this;
    }

    public BuildingHighlightOptions buildingInformationReceivedListener(OnBuildingInformationReceivedListener listener) {
        m_onBuildingInformationReceivedListener = listener;
        return this;
    }

    public SelectionMode getSelectionMode() {
        return m_selectionMode;
    }

    public LatLng getSelectionLocation() {
        return m_selectionLocation;
    }

    public Point getSelectionScreenPoint() {
        return m_selectionScreenPoint;
    }

    /**
     * Returns the color set for this BuildingHighlightOptions object.
     *
     * @return The highlight color as a 32-bit ARGB color.
     */
    public int getColor() {
        return m_colorARGB;
    }

    public boolean getShouldCreateView() { return m_shouldCreateView; }

    public OnBuildingInformationReceivedListener getOnBuildingInformationReceivedListener() { return m_onBuildingInformationReceivedListener; }

}
