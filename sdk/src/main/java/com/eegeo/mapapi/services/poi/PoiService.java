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
        PoiSearch search = new PoiSearch(m_poiApi);
        search.beginTextSearch(options);
        return search;
    }

    @UiThread
    public PoiSearch searchTag(final TagSearchOptions options) {
        PoiSearch search = new PoiSearch(m_poiApi);
        search.beginTagSearch(options);
        return search;
    }

    @UiThread
    public PoiSearch searchAutocomplete(final AutocompleteOptions options) {
        PoiSearch search = new PoiSearch(m_poiApi);
        search.beginAutocompleteSearch(options);
        return search;
    }
}

