package com.eegeo.mapapi.services.poi;

import com.eegeo.mapapi.geometry.LatLng;


/**
 * A set of parameters for a free-text search.
 */
public final class TextSearchOptions {

    private String m_query;
    private LatLng m_center;

    private boolean m_useRadius = false;
    private double m_radius = 0.0;

    private boolean m_useNumber = false;
    private int m_number = 0;

    private boolean m_useMinScore = false;
    private double m_minScore = 0.0;

    private boolean m_useIndoorId = false;
    private String m_indoorId = "";

    private boolean m_useFloorNumber = false;
    private int m_floorNumber = 0;

    private boolean m_useFloorDropoff = false;
    private int m_floorDropoff = 0;

    private OnPoiSearchCompletedListener m_onPoiSearchCompletedListener = null;


    /**
     * @param query The text to search for.
     * @param center The latitude and longitude to search around.
     */
    public TextSearchOptions(String query, LatLng center) {
        this.m_query = query;
        this.m_center = center;
    }

    /**
     * Sets the search result boost radius around the center.
     *
     * @param radius The search result boost radius in metres.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions radius(double radius) {
        this.m_radius = radius;
        this.m_useRadius = true;
        return this;
    }

    /**
     * Sets the maximum number of search results to return.
     *
     * @param number The search result limit.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions number(int number) {
        this.m_number = number;
        this.m_useNumber = true;
        return this;
    }

    /**
     * Sets the minimum score of results. The higher this is set, the fewer results will match.
     *
     * @param minScore The minimum acceptable score for results.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions minScore(double minScore) {
        this.m_minScore = minScore;
        this.m_useMinScore = true;
        return this;
    }

    /**
     * Sets the ID of the indoor map to search in. If not specified, searches outdoors.
     *
     * @param indoorId The indoor map ID to search in.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions indoorId(String indoorId) {
        this.m_indoorId = indoorId;
        this.m_useIndoorId = true;
        return this;
    }

    /**
     * Sets the floor number to search on. If searching indoors and not specified, defaults to floor 0.
     *
     * @param floorNumber The floor number to search on.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions floorNumber(int floorNumber) {
        this.m_floorNumber = floorNumber;
        this.m_useFloorNumber = true;
        return this;
    }

    /**
     * Sets the floor dropoff. Defaults to 15.
     *
     * @param floorDropoff The number of floors above and below to search on.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions floorDropoff(int floorDropoff) {
        this.m_floorDropoff = floorDropoff;
        this.m_useFloorDropoff = true;
        return this;
    }

    /**
     * Sets a listener to receive search results when the search completes.
     *
     * @param onPoiSearchCompletedListener A listener implementing the OnPoiSearchCompletedListener interface.
     * @return This TextSearchOptions object.
     */
    public TextSearchOptions onPoiSearchCompletedListener(OnPoiSearchCompletedListener onPoiSearchCompletedListener) {
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

    double getMinScore() {
        return m_minScore;
    }

    String getIndoorId() {
        return m_indoorId;
    }

    int getFloorNumber() {
        return m_floorNumber;
    }

    int getFloorDropoff() {
        return m_floorDropoff;
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

    boolean usesMinScore() {
        return m_useMinScore;
    }

    boolean usesIndoorId() {
        return m_useIndoorId;
    }

    boolean usesFloorNumber() {
        return m_useFloorNumber;
    }

    boolean usesFloorDropoff() {
        return m_useFloorDropoff;
    }
}

