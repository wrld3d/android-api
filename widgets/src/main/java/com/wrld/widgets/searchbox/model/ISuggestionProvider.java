package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.Query;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

public interface ISuggestionProvider {
    String getSuggestionTitleFormatting();
    void getSuggestions(String queryText, Object queryContext);
    void cancelSuggestions();
    void addSuggestionsReceivedCallback(QueryResultsReadyCallback queryResultsReadyCallback);
    void removeSuggestionsReceivedCallback(QueryResultsReadyCallback queryResultsReadyCallback);

    SearchResultViewFactory getSuggestionViewFactory();
}
