package com.eegeo.mapapi.services.poi;

import java.util.concurrent.Callable;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;


/**
 * A handle to an ongoing search.
 */
public class PoiSearch extends NativeApiObject {

    private PoiApi m_poiApi;
    private OnPoiSearchCompletedListener m_callback = null;

    @UiThread
    PoiSearch(final PoiApi poiApi, OnPoiSearchCompletedListener callback, Callable<Integer> beginSearchCallable) {
        super(poiApi.getNativeRunner(), poiApi.getUiRunner(), beginSearchCallable);

        m_poiApi = poiApi;
        m_callback = callback;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_poiApi.register(PoiSearch.this, getNativeHandle());
            }
        });
    }

    /**
     * Cancels the current search if it has not yet been completed.
     */
    @UiThread
    public void cancel() {
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.cancelSearch(getNativeHandle());
            }
        });
    }

    void returnSearchResults(PoiSearchResponse searchResults) {
        m_callback.onPoiSearchCompleted(searchResults);
    }
}
