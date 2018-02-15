package com.wrld.widgets.searchbox.model;


import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

public interface SearchProvider {
    String getTitle();
    void getSearchResults(String queryText, Object queryContext);
    void cancelSearch();

    void addSearchCompletedCallback(SearchProviderResultsReadyCallback searchProviderResultsReadyCallback);
    void removeSearchCompletedCallback(SearchProviderResultsReadyCallback searchProviderResultsReadyCallback);

    ISearchResultViewFactory getResultViewFactory();
}

