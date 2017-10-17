package com.eegeo.mapapi.services;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;


public class PoiApi {

    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<PoiSearch> m_nativeHandleToPoiSearch = new SparseArray<>();


    public PoiApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    int createSearch(String query, LatLng center, PoiSearchOptions options) {
        int searchId = nativeSearch(
                m_jniEegeoMapApiPtr,
                query,
                center.latitude,
                center.longitude,
                options.usesRadius(), options.getRadius(),
                options.usesNumber(), options.getNumber(),
                options.usesMinScore(), options.getMinScore(),
                options.usesIndoorId(), options.getIndoorId(),
                options.usesFloorNumber(), options.getFloorNumber(),
                options.usesFloorDropoff(), options.getFloorDropoff());

        return searchId;
    }

    @WorkerThread
    public void register(PoiSearch poiSearch) {
        int nativeHandle = poiSearch.getNativeHandleUrgh();
        m_nativeHandleToPoiSearch.put(nativeHandle, poiSearch);
    }

    @WorkerThread
    public void unregister(PoiSearch poiSearch) {
        m_nativeHandleToPoiSearch.remove(poiSearch.getNativeHandleUrgh());
    }


    @WorkerThread
    public void notifySearchComplete(final int nativeHandle, final String searchResults) {
        if (m_nativeHandleToPoiSearch.get(nativeHandle) != null) {
            returnSearchResults(nativeHandle, searchResults);
        }
    }

    @WorkerThread
    public void returnSearchResults(final int nativeHandle, final String searchResults) {
        final PoiSearch poiSearch = m_nativeHandleToPoiSearch.get(nativeHandle);

        if (poiSearch == null)
            throw new NullPointerException("PoiSearch object not found for nativeHandle");

        poiSearch.returnSearchResults(searchResults);
        m_nativeHandleToPoiSearch.remove(nativeHandle);
    }

    @UiThread
    INativeMessageRunner getNativeRunner() {
        return m_nativeRunner;
    }

    @UiThread
    IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    private native int nativeSearch(
            long jniEegeoMapApiPtr,
            String query,
            double latitude,
            double longitude,
            boolean useRadius, double radius,
            boolean useNumber, int number,
            boolean useMinScore, double minScore,
            boolean useIndoorId, String indoorId,
            boolean useFloor, int floor,
            boolean useFloorDropoff, int floorDropoff);
}

