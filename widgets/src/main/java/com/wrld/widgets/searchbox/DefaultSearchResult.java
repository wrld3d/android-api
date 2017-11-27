// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved
package com.wrld.widgets.searchbox;

import android.graphics.drawable.Icon;
import java.util.HashMap;

class DefaultSearchResult implements SearchResult {

    private String m_title;

    private HashMap<String, SearchResultProperty> m_additionalProperties;

    DefaultSearchResult(String title, SearchResultProperty... additionalProperties) {
        m_title = title;

        m_additionalProperties = new HashMap<String, SearchResultProperty>();

        for(SearchResultProperty property : additionalProperties){
            m_additionalProperties.put(property.getKey(), property);
        }
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