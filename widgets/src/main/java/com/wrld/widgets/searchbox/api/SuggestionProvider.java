package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryCompletedCallback;

public interface SuggestionProvider extends SearchProvider {

    String getSuggestionTitleFormatting();

    void getSuggestions(String text);

    void addSuggestionsReceivedCallback(QueryCompletedCallback queryCompletedCallback);
    void removeSuggestionsReceivedCallback(QueryCompletedCallback queryCompletedCallback);

    void setSuggestionViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getSuggestionViewFactory();
}
