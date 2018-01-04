package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

public interface SearchProvider {
    String getTitle();
    void getSearchResults(Query query);

    void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback);
    void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback);

    void setResultViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getResultViewFactory();
}
