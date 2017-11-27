// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.searchproviders;

import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.SearchResult;
import com.wrld.widgets.searchbox.SearchResultProperty;
import com.wrld.widgets.searchbox.SearchResultStringProperty;

import java.util.HashMap;

class PoiSearchResultModel implements SearchResult {

    private String m_title;

    public String DescriptionKey = "Description";

    private HashMap<String, SearchResultProperty> m_additionalProperties;

    public PoiSearchResultModel(String title, LatLng latLng, String description){
        m_title = title;

        m_additionalProperties = new HashMap<String, SearchResultProperty>();
        m_additionalProperties.put(SearchPropertyLatLng.Key, new SearchPropertyLatLng(latLng));
        m_additionalProperties.put(DescriptionKey, new SearchResultStringProperty(DescriptionKey, description));
    }

    @Override
    public String getTitle() {
        return m_title;
    }

    @Override
    public SearchResultProperty getProperty(String propertyKey) {
        if(m_additionalProperties.containsKey(propertyKey)) {
            return m_additionalProperties.get(propertyKey);
        }
        return null;
    }
}
