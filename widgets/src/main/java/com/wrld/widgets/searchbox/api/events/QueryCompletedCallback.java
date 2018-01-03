package com.wrld.widgets.searchbox.api.events;

import com.wrld.widgets.searchbox.SearchResult;

public interface QueryCompletedCallback {
    void onQueryCompleted(SearchResult[] returnedResults);
    void onQueryCancelled();
}

