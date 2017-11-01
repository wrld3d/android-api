package com.eegeo.mapapi.services.poi;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;


public class PoiService {

    private PoiApi m_poiApi;


    public PoiService(PoiApi poiApi) {
        this.m_poiApi = poiApi;
    }


    @UiThread
    public PoiSearch searchText(final TextSearchOptions options) {
        return m_poiApi.searchText(options);
    }

    @UiThread
    public PoiSearch searchTag(final TagSearchOptions options) {
        return m_poiApi.searchTag(options);
    }

    @UiThread
    public PoiSearch searchAutocomplete(final AutocompleteOptions options) {
        return m_poiApi.searchAutocomplete(options);
    }
}

