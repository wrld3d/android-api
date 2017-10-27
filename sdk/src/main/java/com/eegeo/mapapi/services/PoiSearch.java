package com.eegeo.mapapi.services;

import java.util.concurrent.Callable;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

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
    void beginTextSearch(final TextSearchOptions options) {
        this.m_callback = options.getOnPoiSearchCompletedListener();
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.beginTextSearch(getNativeHandle(), options);
            }
        });
    }

    @UiThread
    void beginTagSearch(final TagSearchOptions options) {
        this.m_callback = options.getOnPoiSearchCompletedListener();
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.beginTagSearch(getNativeHandle(), options);
            }
        });
    }

    @UiThread
    void beginAutocompleteSearch(final AutocompleteOptions options) {
        this.m_callback = options.getOnPoiSearchCompletedListener();
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_poiApi.beginAutocompleteSearch(getNativeHandle(), options);
            }
        });
    }

    void returnSearchResults(PoiSearchResults searchResults) {
        m_callback.onPoiSearchCompleted(searchResults);
    }
}
