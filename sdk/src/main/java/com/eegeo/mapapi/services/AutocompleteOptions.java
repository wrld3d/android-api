package com.eegeo.mapapi.services;

import com.eegeo.mapapi.geometry.LatLng;


public final class AutocompleteOptions {

    private String m_query;
    private LatLng m_center;

    private boolean m_useNumber = false;
    private int m_number = 0;

    private OnPoiSearchCompletedListener m_onPoiSearchCompletedListener = null;


    public AutocompleteOptions(String query, LatLng center) {
        this.m_query = query;
        this.m_center = center;
    }


    public AutocompleteOptions number(int number) {
        this.m_number = number;
        this.m_useNumber = true;
        return this;
    }

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

