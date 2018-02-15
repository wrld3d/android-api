package com.wrld.widgets.searchbox.model;

import java.util.List;

public interface SuggestionQueryModelListener
{
    void onSuggestionQueryStarted(final SearchQuery query);
    void onSuggestionQueryCompleted(final SearchQuery query, final List<SearchProviderQueryResult> results);
    void onSuggestionQueryCancelled(final SearchQuery query);
}
