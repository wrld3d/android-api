package com.wrld.searchproviders;

import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.api.SearchResultProperty;

public class SearchPropertyLatLng implements SearchResultProperty<LatLng> {

    public static String Key = "LatLng";

    private LatLng m_latLng;

    public SearchPropertyLatLng(LatLng latLng){
        m_latLng = latLng;
    }

    @Override
    public String getKey() {
        return Key;
    }

    @Override
    public LatLng getValue() {
        return m_latLng;
    }

    @Override
    public int compareTo(SearchResultProperty<LatLng> other) {
        return 0;
    }
}
