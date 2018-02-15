package com.wrld.widgets.searchbox.model;

public interface SearchProviderResultsReadyCallback {
    void onQueryCompleted(SearchResult[] returnedResults, Boolean success);
}

