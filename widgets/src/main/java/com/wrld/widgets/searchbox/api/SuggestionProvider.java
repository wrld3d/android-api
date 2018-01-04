package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

public interface SuggestionProvider extends SearchProvider {
    String getSuggestionTitleFormatting();
    void getSuggestions(com.wrld.widgets.searchbox.api.Query query);

    void addSuggestionsReceivedCallback(QueryResultsReadyCallback queryResultsReadyCallback);
    void removeSuggestionsReceivedCallback(QueryResultsReadyCallback queryResultsReadyCallback);

    void setSuggestionViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getSuggestionViewFactory();
}
