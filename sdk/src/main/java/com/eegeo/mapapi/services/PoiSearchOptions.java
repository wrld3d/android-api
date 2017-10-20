package com.eegeo.mapapi.services;


// TODO: Rename to FreeTextSearchOptions?
public final class PoiSearchOptions {

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


    public PoiSearchOptions() {

    }


    public PoiSearchOptions radius(double radius) {
        this.m_radius = radius;
        this.m_useRadius = true;
        return this;
    }

    public PoiSearchOptions number(int number) {
        this.m_number = number;
        this.m_useNumber = true;
        return this;
    }

    public PoiSearchOptions minScore(double minScore) {
        this.m_minScore = minScore;
        this.m_useMinScore = true;
        return this;
    }

    public PoiSearchOptions indoorId(String indoorId) {
        this.m_indoorId = indoorId;
        this.m_useIndoorId = true;
        return this;
    }

    public PoiSearchOptions floorNumber(int floorNumber) {
        this.m_floorNumber = floorNumber;
        this.m_useFloorNumber = true;
        return this;
    }

    public PoiSearchOptions floorDropoff(int floorDropoff) {
        this.m_floorDropoff = floorDropoff;
        this.m_useFloorDropoff = true;
        return this;
    }

    public PoiSearchOptions onPoiSearchCompletedListener(OnPoiSearchCompletedListener onPoiSearchCompletedListener) {
        this.m_onPoiSearchCompletedListener = onPoiSearchCompletedListener;
        return this;
    }


    public double getRadius() {
        return m_radius;
    }

    public int getNumber() {
        return m_number;
    }

    public double getMinScore() {
        return m_minScore;
    }

    public String getIndoorId() {
        return m_indoorId;
    }

    public int getFloorNumber() {
        return m_floorNumber;
    }

    public int getFloorDropoff() {
        return m_floorDropoff;
    }

    public OnPoiSearchCompletedListener getOnPoiSearchCompletedListener() {
        return m_onPoiSearchCompletedListener;
    }


    public boolean usesRadius() {
        return m_useRadius;
    }

    public boolean usesNumber() {
        return m_useNumber;
    }

    public boolean usesMinScore() {
        return m_useMinScore;
    }

    public boolean usesIndoorId() {
        return m_useIndoorId;
    }

    public boolean usesFloorNumber() {
        return m_useFloorNumber;
    }

    public boolean usesFloorDropoff() {
        return m_useFloorDropoff;
    }
}

