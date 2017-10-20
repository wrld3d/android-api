package com.eegeo.mapapi.services;


public final class AutocompleteOptions {

    private boolean m_useNumber = false;
    private int m_number = 0;

    private OnPoiSearchCompletedListener m_onPoiSearchCompletedListener = null;


    public AutocompleteOptions() {

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


    public int getNumber() {
        return m_number;
    }

    public OnPoiSearchCompletedListener getOnPoiSearchCompletedListener() {
        return m_onPoiSearchCompletedListener;
    }


    public boolean usesNumber() {
        return m_useNumber;
    }
}

