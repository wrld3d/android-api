package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryCompletedCallback;

public interface SearchProvider {
    String getTitle();
    void getSearchResults(String query);
    boolean hasActiveRequest();
    void cancelActiveRequest();

    void addSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback);
    void removeSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback);

    void setResultViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getResultViewFactory();
}
