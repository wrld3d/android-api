// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved
package com.wrld.widgets.searchbox;

import android.graphics.drawable.Icon;
import java.util.HashMap;

class DefaultSearchResult implements SearchResult {

    private String m_title;
    private SearchResultStringProperty m_description;

    private HashMap<String, SearchResultProperty> m_additionalProperties;

    DefaultSearchResult(String title, String description, SearchResultProperty... additionalProperties) {
        m_title = title;
        m_description = new SearchResultStringProperty("Description", description);

        m_additionalProperties = new HashMap<String, SearchResultProperty>();
        m_additionalProperties.put("Description", m_description);

        for(SearchResultProperty property : additionalProperties){
            m_additionalProperties.put(property.getKey(), property);
        }
    }

    @Override
    public String getTitle() {
        return m_title;
    }

    @Override
    public SearchResultProperty<String> getDescription() {
        return m_description;
    }

    @Override
    public SearchResultProperty<Icon> getIcon() {
        return null;
    }

    @Override
    public SearchResultProperty getProperty(String propertyKey) {
        if(m_additionalProperties.containsKey(propertyKey)) {
            return m_additionalProperties.get(propertyKey);
        }
        return null;
    }
}
