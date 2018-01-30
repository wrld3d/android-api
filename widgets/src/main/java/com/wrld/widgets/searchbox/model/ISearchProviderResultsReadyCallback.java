package com.wrld.widgets.searchbox.model;

public interface ISearchProviderResultsReadyCallback {
    void onQueryCompleted(ISearchResult[] returnedResults, Boolean success);
}

