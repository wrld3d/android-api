package com.wrld.widgets.searchbox.api.events;

import com.wrld.widgets.searchbox.api.SearchResult;

public interface SearchResultSelectedCallback {
    void onSelection(SearchResult selectedResult);
}
