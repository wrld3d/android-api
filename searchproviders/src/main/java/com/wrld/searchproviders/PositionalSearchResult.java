package com.wrld.searchproviders;

import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.model.DefaultSearchResult;
import com.wrld.widgets.searchbox.model.SearchResultPropertyString;
import com.wrld.widgets.searchbox.view.DefaultSearchResultViewFactory;

class PositionalSearchResult extends DefaultSearchResult {

    public static String IndoorMapKey = "IndoorMap";
    public static String IndoorFloorKey = "IndoorFloor";

    public PositionalSearchResult(String title, LatLng latLng, String description){
        super(title, new SearchPropertyLatLng(latLng), new SearchResultPropertyString(DefaultSearchResultViewFactory.DescriptionKey, description));
    }

    public PositionalSearchResult(String title,
                                  LatLng latLng,
                                  String description,
                                  String indoorMapId,
                                  int indoorMapFloor,
                                  int iconResource){
        super(title,
                new SearchPropertyLatLng(latLng),
                new SearchResultPropertyString(DefaultSearchResultViewFactory.DescriptionKey, description),
                new SearchResultPropertyString(IndoorMapKey, indoorMapId),
                new SearchResultPropertyInt(IndoorFloorKey, indoorMapFloor),
                new SearchResultPropertyInt(DefaultSearchResultViewFactory.IconResourceKey, iconResource));
    }
}
