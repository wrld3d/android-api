package com.eegeo.mapapi.services;

import java.util.concurrent.Callable;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;


public class PoiSearch extends NativeApiObject {

    private PoiApi m_poiApi;
    private OnPoiSearchCompletedListener m_callback = null;

    @UiThread
    PoiSearch(final PoiApi poiApi) {
        super(poiApi.getNativeRunner(), poiApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return poiApi.createSearch();
                    }
                });

        m_poiApi = poiApi;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_poiApi.register(PoiSearch.this, getNativeHandle());
            }
        });
    }

    @UiThread
    public void cancel() {
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.cancelSearch(getNativeHandle());
            }
        });
    }

    @UiThread
    void beginTextSearch(final String query, final LatLng center, final PoiSearchOptions options) {
        this.m_callback = options.getOnPoiSearchCompletedListener();
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.beginTextSearch(getNativeHandle(), query, center, options);
            }
        });
    }

    @UiThread
    void beginTagSearch(final String tag, final LatLng center, final TagSearchOptions options) {
        this.m_callback = options.getOnPoiSearchCompletedListener();
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.beginTagSearch(getNativeHandle(), tag, center, options);
            }
        });
    }

    void returnSearchResults(PoiSearchResult searchResults) {
        m_callback.onPoiSearchCompleted(searchResults);
    }
}
