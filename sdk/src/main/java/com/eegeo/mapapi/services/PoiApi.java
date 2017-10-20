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

    @WorkerThread
    int createSearch() {
        int searchId = nativeCreateSearch(m_jniEegeoMapApiPtr);
        return searchId;
    }

    @WorkerThread
    void beginTextSearch(final int searchNativeHandle, final String query, final LatLng center, final PoiSearchOptions options) {
        nativeBeginTextSearch(
                m_jniEegeoMapApiPtr,
                searchNativeHandle,
                query,
                center.latitude,
                center.longitude,
                options.usesRadius(), options.getRadius(),
                options.usesNumber(), options.getNumber(),
                options.usesMinScore(), options.getMinScore(),
                options.usesIndoorId(), options.getIndoorId(),
                options.usesFloorNumber(), options.getFloorNumber(),
                options.usesFloorDropoff(), options.getFloorDropoff());
    }

    @WorkerThread
    void beginTagSearch(final int searchNativeHandle, final String tag, final LatLng center, final TagSearchOptions options) {
        nativeBeginTagSearch(
                m_jniEegeoMapApiPtr,
                searchNativeHandle,
                tag,
                center.latitude,
                center.longitude,
                options.usesRadius(), options.getRadius(),
                options.usesNumber(), options.getNumber());
    }

    @WorkerThread
    void cancelSearch(final int searchNativeHandle) {
        nativeCancelSearch(m_jniEegeoMapApiPtr, searchNativeHandle);
        m_nativeHandleToPoiSearch.remove(searchNativeHandle);
    }

    @WorkerThread
    void register(PoiSearch poiSearch, int nativeHandle) {
        m_nativeHandleToPoiSearch.put(nativeHandle, poiSearch);
    }

    @WorkerThread
    void unregister(int nativeHandle) {
        m_nativeHandleToPoiSearch.remove(nativeHandle);
    }


    @WorkerThread
    public void notifySearchComplete(final int nativeHandle, final boolean succeeded, final String searchResults) {
        PoiSearchResult result = new PoiSearchResult(succeeded, searchResults);
        if (m_nativeHandleToPoiSearch.get(nativeHandle) != null) {
            returnSearchResults(nativeHandle, result);
        }
    }

    @WorkerThread
    void returnSearchResults(final int nativeHandle, final PoiSearchResult searchResults) {
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

    private native int nativeCreateSearch(long jniEegeoMapApiPtr);

    private native void nativeBeginTextSearch(
            long jniEegeoMapApiPtr,
            int searchNativeHandle,
            String query,
            double latitude,
            double longitude,
            boolean useRadius, double radius,
            boolean useNumber, int number,
            boolean useMinScore, double minScore,
            boolean useIndoorId, String indoorId,
            boolean useFloor, int floor,
            boolean useFloorDropoff, int floorDropoff);

    private native void nativeBeginTagSearch(
            long jniEegeoMapApiPtr,
            int searchNativeHandle,
            String query,
            double latitude,
            double longitude,
            boolean useRadius, double radius,
            boolean useNumber, int number);

    private native void nativeCancelSearch(long jniEegeoMapApiPtr, int searchNativeHandle);
}

