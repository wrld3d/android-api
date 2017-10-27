package com.eegeo.mapapi.services.poi;

import com.eegeo.mapapi.geometry.LatLng;


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


    public TextSearchOptions(String query, LatLng center) {
        this.m_query = query;
        this.m_center = center;
    }


    public TextSearchOptions radius(double radius) {
        this.m_radius = radius;
        this.m_useRadius = true;
        return this;
    }

    public TextSearchOptions number(int number) {
        this.m_number = number;
        this.m_useNumber = true;
        return this;
    }

    public TextSearchOptions minScore(double minScore) {
        this.m_minScore = minScore;
        this.m_useMinScore = true;
        return this;
    }

    public TextSearchOptions indoorId(String indoorId) {
        this.m_indoorId = indoorId;
        this.m_useIndoorId = true;
        return this;
    }

    public TextSearchOptions floorNumber(int floorNumber) {
        this.m_floorNumber = floorNumber;
        this.m_useFloorNumber = true;
        return this;
    }

    public TextSearchOptions floorDropoff(int floorDropoff) {
        this.m_floorDropoff = floorDropoff;
        this.m_useFloorDropoff = true;
        return this;
    }

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

