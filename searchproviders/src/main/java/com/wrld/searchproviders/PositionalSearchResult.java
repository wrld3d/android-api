package com.wrld.searchproviders;

import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.DefaultSearchResult;
import com.wrld.widgets.searchbox.SearchResultPropertyString;

class PositionalSearchResult extends DefaultSearchResult {

    public static String DescriptionKey = "Description";

    public PositionalSearchResult(String title, LatLng latLng, String description){
        super(title, new SearchPropertyLatLng(latLng), new SearchResultPropertyString(DescriptionKey, description));
    }
}
