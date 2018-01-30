package com.wrld.widgets.searchbox.model;


import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

public interface ISearchProvider {
    String getTitle();
    void getSearchResults(String queryText, Object queryContext);
    void cancelSearch();

    void addSearchCompletedCallback(ISearchProviderResultsReadyCallback searchProviderResultsReadyCallback);
    void removeSearchCompletedCallback(ISearchProviderResultsReadyCallback searchProviderResultsReadyCallback);

    ISearchResultViewFactory getResultViewFactory();
}

