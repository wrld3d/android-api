package com.eegeo.mapapi.services;

import java.util.concurrent.Callable;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;


public class PoiSearch extends NativeApiObject {

    private PoiApi m_poiApi;
    private PoiSearchOptions m_options;

    @UiThread
    PoiSearch(
            final PoiApi poiApi,
            final String query,
            final LatLng center,
            final PoiSearchOptions options) {

        super(poiApi.getNativeRunner(), poiApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return poiApi.createSearch(query, center, options);
                    }
                });

        m_poiApi = poiApi;
        m_options = options;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_poiApi.register(PoiSearch.this);
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

    @WorkerThread
    int getNativeHandleUrgh() {
        if (!hasNativeHandle())
            throw new IllegalStateException("Native handle not available");

        return getNativeHandle();
    }

    void returnSearchResults(PoiSearchResult searchResults) {
        m_options.getOnPoiSearchCompletedListener().onPoiSearchCompleted(searchResults);
    }
}
