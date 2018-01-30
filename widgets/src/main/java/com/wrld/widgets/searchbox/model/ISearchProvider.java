package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

public interface ISearchProvider {
    String getTitle();
    void getSearchResults(String queryText, Object queryContext);
    void cancelSearch();

    void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback);
    void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback);

    SearchResultViewFactory getResultViewFactory();
}

