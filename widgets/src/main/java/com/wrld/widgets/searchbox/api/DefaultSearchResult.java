package com.wrld.widgets.searchbox.api;

import java.util.HashMap;

public class DefaultSearchResult implements SearchResult {

    private String m_title;

    private HashMap<String, SearchResultProperty> m_additionalProperties;

    public DefaultSearchResult(String title, SearchResultProperty... additionalProperties) {
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
    public boolean hasProperty(String key){
        return m_additionalProperties.containsKey(key);
    }

    @Override
    public SearchResultProperty getProperty(String propertyKey) {
        if(m_additionalProperties.containsKey(propertyKey)) {
            return m_additionalProperties.get(propertyKey);
        }
        return null;
    }

    protected void addProperty(String key, SearchResultProperty property){
        m_additionalProperties.put(key, property);
    }
}
