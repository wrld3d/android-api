package com.eegeo.mapapi.services.poi;

import java.util.concurrent.Callable;
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


    @UiThread
    public PoiSearch searchText(final TextSearchOptions options) {
        PoiSearch search = new PoiSearch(this, options.getOnPoiSearchCompletedListener(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return beginTextSearch(options);
                    }
                });
        return search;
    }

    @UiThread
    public PoiSearch searchTag(final TagSearchOptions options) {
        PoiSearch search = new PoiSearch(this, options.getOnPoiSearchCompletedListener(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return beginTagSearch(options);
                    }
                });
        return search;
    }

    @UiThread
    public PoiSearch searchAutocomplete(final AutocompleteOptions options) {
        PoiSearch search = new PoiSearch(this, options.getOnPoiSearchCompletedListener(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return beginAutocompleteSearch(options);
                    }
                });
        return search;
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


    @WorkerThread
    int beginTextSearch(final TextSearchOptions options) {
        return nativeBeginTextSearch(
                m_jniEegeoMapApiPtr,
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
    int beginTagSearch(final TagSearchOptions options) {
        return nativeBeginTagSearch(
                m_jniEegeoMapApiPtr,
                options.getQuery(),
                options.getCenter().latitude,
                options.getCenter().longitude,
                options.usesRadius(), options.getRadius(),
                options.usesNumber(), options.getNumber());
    }

    @WorkerThread
    int beginAutocompleteSearch(final AutocompleteOptions options) {
        return nativeBeginAutocompleteSearch(
                m_jniEegeoMapApiPtr,
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
    private native int nativeBeginTextSearch(
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

    @WorkerThread
    private native int nativeBeginTagSearch(
            long jniEegeoMapApiPtr,
            String query,
            double latitude,
            double longitude,
            boolean useRadius, double radius,
            boolean useNumber, int number);

    @WorkerThread
    private native int nativeBeginAutocompleteSearch(
            long jniEegeoMapApiPtr,
            String query,
            double latitude,
            double longitude,
            boolean useNumber, int number);

    @WorkerThread
    private native void nativeCancelSearch(long jniEegeoMapApiPtr, int searchNativeHandle);
}

