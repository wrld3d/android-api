package com.eegeo.mapapi.services.poi;

import com.eegeo.mapapi.geometry.LatLng;


/**
 * A set of parameters for an autocomplete search. 
 */
public final class AutocompleteOptions {

    private String m_query;
    private LatLng m_center;

    private boolean m_useNumber = false;
    private int m_number = 0;

    private OnPoiSearchCompletedListener m_onPoiSearchCompletedListener = null;


    /**
     * @param query The text to search for.
     * @param center The latitude and longitude to search around.
     */
    public AutocompleteOptions(String query, LatLng center) {
        this.m_query = query;
        this.m_center = center;
    }


    /**
     * Sets the maximum number of search results to return.
     *
     * @param number The search result limit.
     * @return This AutocompleteOptions object.
     */
    public AutocompleteOptions number(int number) {
        this.m_number = number;
        this.m_useNumber = true;
        return this;
    }


    /**
     * Sets a listener to receive search results when the search completes.
     *
     * @param onPoiSearchCompletedListener A listener implementing the OnPoiSearchCompletedListener interface.
     * @return This AutocompleteOptions object.
     */
    public AutocompleteOptions onPoiSearchCompletedListener(OnPoiSearchCompletedListener onPoiSearchCompletedListener) {
        this.m_onPoiSearchCompletedListener = onPoiSearchCompletedListener;
        return this;
    }


    String getQuery() {
        return m_query;
    }

    LatLng getCenter() {
        return m_center;
    }

    int getNumber() {
        return m_number;
    }

    OnPoiSearchCompletedListener getOnPoiSearchCompletedListener() {
        return m_onPoiSearchCompletedListener;
    }


    boolean usesNumber() {
        return m_useNumber;
    }
}

