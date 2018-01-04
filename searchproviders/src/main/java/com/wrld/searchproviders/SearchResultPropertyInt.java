package com.wrld.searchproviders;

import com.wrld.widgets.searchbox.api.SearchResultProperty;

public class SearchResultPropertyInt implements SearchResultProperty<Integer> {

    private String m_key;
    private Integer m_value;

    public SearchResultPropertyInt(String key, Integer value){

        m_key = key;
        m_value = value;
    }

    @Override
    public String getKey() {
        return m_key;
    }

    @Override
    public Integer getValue() {
        return m_value;
    }

    @Override
    public int compareTo(SearchResultProperty<Integer> other) {
        return 0;
    }
}
