// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.searchproviders;

import com.wrld.widgets.searchbox.SearchResultProperty;

public class SearchResultPropertyDouble implements SearchResultProperty<Double> {

    private String m_key;
    private Double m_value;

    public SearchResultPropertyDouble(String key, Double value){

        m_key = key;
        m_value = value;
    }

    @Override
    public String getKey() {
        return m_key;
    }

    @Override
    public Double getValue() {
        return m_value;
    }

    @Override
    public int compareTo(SearchResultProperty<Double> other) {
        return 0;
    }
}
