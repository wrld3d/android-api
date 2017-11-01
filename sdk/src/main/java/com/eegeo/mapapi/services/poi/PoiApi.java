package com.eegeo.mapapi.services.poi;

import java.util.List;

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
    void beginTextSearch(final int searchNativeHandle, final TextSearchOptions options) {
        nativeBeginTextSearch(
                m_jniEegeoMapApiPtr,
                searchNativeHandle,
                options.getQuery(),
                options.getCenter().latitude,
                options.getCenter().longitude,
                options.usesRadius(), options.getRadius(),
                options.usesNumber(), options.getNumber(),
                options.usesMinScore(), options.getMinScore(),
                options.usesIndoorId(), options.getIndoorId(),
                options.usesFloorNumber(), options.getFloorNumber(),
                options.usesFloorDropoff(), options.getFloorDropoff());
    }

    @WorkerThread
    void beginTagSearch(final int searchNativeHandle, final TagSearchOptions options) {
        nativeBeginTagSearch(
                m_jniEegeoMapApiPtr,
                searchNativeHandle,
                options.getQuery(),
                options.getCenter().latitude,
                options.getCenter().longitude,
                options.usesRadius(), options.getRadius(),
                options.usesNumber(), options.getNumber());
    }

    @WorkerThread
    void beginAutocompleteSearch(final int searchNativeHandle, final AutocompleteOptions options) {
        nativeBeginAutocompleteSearch(
                m_jniEegeoMapApiPtr,
                searchNativeHandle,
                options.getQuery(),
                options.getCenter().latitude,
                options.getCenter().longitude,
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
    public void notifySearchComplete(final int nativeHandle, final boolean succeeded, final List<PoiSearchResult> searchResults) {
        PoiSearchResponse result = new PoiSearchResponse(succeeded, searchResults);
        if (m_nativeHandleToPoiSearch.get(nativeHandle) != null) {
            returnSearchResults(nativeHandle, result);
        }
    }

    @WorkerThread
    void returnSearchResults(final int nativeHandle, final PoiSearchResponse searchResults) {
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

    private native void nativeBeginAutocompleteSearch(
            long jniEegeoMapApiPtr,
            int searchNativeHandle,
            String query,
            double latitude,
            double longitude,
            boolean useNumber, int number);

    private native void nativeCancelSearch(long jniEegeoMapApiPtr, int searchNativeHandle);
}

