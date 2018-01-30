package com.wrld.widgets.searchbox.model;

import java.util.List;

public interface ISearchWidgetModelListener
{
    void onSearchQueryStarted(final SearchQuery query);
    void onSearchQueryCompleted(final SearchQuery query, final List<SearchProviderQueryResult> results);

    void onSuggestionQueryStarted(final SearchQuery query);
    void onSuggestionQueryCompleted(final SearchQuery query, final List<SearchProviderQueryResult> results);

    void onSearchQueryCancelled();
    void onSearchResultsCleared();
}
