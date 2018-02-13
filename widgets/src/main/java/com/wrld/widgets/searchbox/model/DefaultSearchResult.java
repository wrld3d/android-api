package com.wrld.widgets.searchbox.model;

import java.util.HashMap;

public class DefaultSearchResult implements ISearchResult {

    private String m_title;

    private HashMap<String, SearchResultProperty> m_additionalProperties;
    private OnSearchResultSelectedListener m_listener;

    public DefaultSearchResult(String title, SearchResultProperty... additionalProperties) {
        m_title = title;
        m_listener = null;

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

    @Override
    public void select() {
        if(m_listener != null) {
            m_listener.onSearchResultSelected(this);
        }
    }

    public void setSelectedListener(OnSearchResultSelectedListener listener) {
        m_listener = listener;
    }

    protected void addProperty(String key, SearchResultProperty property){
        m_additionalProperties.put(key, property);
    }
}
