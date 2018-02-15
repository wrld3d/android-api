package com.wrld.widgets.searchbox.model;

import java.util.List;

public interface SearchResultsListener
{
    void onSearchResultsRecieved(final SearchQuery query, final List<SearchProviderQueryResult> results);
    void onSearchResultsCleared();
}