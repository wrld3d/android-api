package com.wrld.widgets.searchbox.api;

public class SearchResultPropertyString implements SearchResultProperty<String> {

    private String m_key;
    private String m_value;

    public SearchResultPropertyString(String key, String value) {
        m_key = key;
        m_value = value;
    }

    @Override
    public String getKey() {
        return m_key;
    }

    @Override
    public String getValue() {
        return m_value;
    }

    @Override
    public int compareTo(SearchResultProperty<String> other) {
        return m_value.compareTo(other.getValue());
    }
}
