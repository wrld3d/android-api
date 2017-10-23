// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

final class SearchResultSetViewModel {
    private SearchResultSet m_set;
    private int m_factoryIndex;

    public SearchResultSet getSearchResultSet() { return m_set; }
    public int getFactoryIndex() { return m_factoryIndex; }

    public SearchResultSetViewModel(int factoryIndex){
        m_factoryIndex = factoryIndex;
    }

    public SearchResultSetViewModel(SearchResultSet set, int factoryIndex){
        m_set = set;
        m_factoryIndex = factoryIndex;
    }
}

final class SearchResultViewModel {

    private SearchResult m_data;
    private SearchResultSetViewModel m_setViewModel;

    public SearchResult getData() { return m_data; }
    public int getFactoryIndex() { return m_setViewModel.getFactoryIndex(); }

    public SearchResultViewModel(SearchResultSetViewModel setViewModel){
        m_setViewModel = setViewModel;
    }

    public SearchResultViewModel(SearchResult data, SearchResultSetViewModel setViewModel){
        m_data = data;
        m_setViewModel = setViewModel;
    }
}
