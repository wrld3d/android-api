package com.eegeo.mapapi.services;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.services.PoiSearch;
import com.eegeo.mapapi.services.PoiApi;


public class PoiService {

    private PoiApi m_poiApi;


    public PoiService(PoiApi poiApi) {
        this.m_poiApi = poiApi;
    }

    @UiThread
    public PoiSearch search(final String query, final LatLng center, final PoiSearchOptions options) {
        PoiSearch search = new PoiSearch(m_poiApi, query, center, options);
        return search;
    }

}

