package com.eegeo.mapapi.services.poi;

import com.eegeo.mapapi.geometry.LatLng;


/**
 * A set of parameters for a tag search.
 */
public final class TagSearchOptions {

    private String m_query;
    private LatLng m_center;

    private boolean m_useRadius = false;
    private double m_radius = 0.0;

    private boolean m_useNumber = false;
    private int m_number = 0;

    private OnPoiSearchCompletedListener m_onPoiSearchCompletedListener = null;


    /**
     * @param tag The tag to search for.
     * @param center The latitude and longitude to search around.
     */
    public TagSearchOptions(String tag, LatLng center) {
        this.m_query = tag;
        this.m_center = center;
    }


    /**
     * Sets the radius around the center to search in.
     *
     * @param radius The search radius in metres.
     * @return This TagSearchOptions object.
     */
    public TagSearchOptions radius(double radius) {
        this.m_radius = radius;
        this.m_useRadius = true;
        return this;
    }

    /**
     * Sets the maximum number of search results to return.
     *
     * @param number The search result limit.
     * @return This TagSearchOptions object.
     */
    public TagSearchOptions number(int number) {
        this.m_number = number;
        this.m_useNumber = true;
        return this;
    }

    /**
     * Sets a listener to receive search results when the search completes.
     *
     * @param onPoiSearchCompletedListener A listener implementing the OnPoiSearchCompletedListener interface.
     * @return This TagSearchOptions object.
     */
    public TagSearchOptions onPoiSearchCompletedListener(OnPoiSearchCompletedListener onPoiSearchCompletedListener) {
        this.m_onPoiSearchCompletedListener = onPoiSearchCompletedListener;
        return this;
    }


    String getQuery() {
        return m_query;
    }

    LatLng getCenter() {
        return m_center;
    }

    double getRadius() {
        return m_radius;
    }

    int getNumber() {
        return m_number;
    }

    OnPoiSearchCompletedListener getOnPoiSearchCompletedListener() {
        return m_onPoiSearchCompletedListener;
    }


    boolean usesRadius() {
        return m_useRadius;
    }

    boolean usesNumber() {
        return m_useNumber;
    }
}

