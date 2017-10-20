package com.eegeo.mapapi.services;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;


public class PoiService {

    private PoiApi m_poiApi;


    public PoiService(PoiApi poiApi) {
        this.m_poiApi = poiApi;
    }

    @UiThread
    public PoiSearch searchText(final String query, final LatLng center, final PoiSearchOptions options) {
        PoiSearch search = new PoiSearch(m_poiApi);
        search.beginTextSearch(query, center, options);
        return search;
    }

    @UiThread
    public PoiSearch searchTag(final String tag, final LatLng center, final TagSearchOptions options) {
        PoiSearch search = new PoiSearch(m_poiApi);
        search.beginTagSearch(tag, center, options);
        return search;
    }
}

