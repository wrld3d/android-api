package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

public interface SuggestionProvider {
    String getSuggestionTitleFormatting();
    void getSuggestions(String queryText, Object queryContext);
    void cancelSuggestions();
    void addSuggestionsReceivedCallback(SearchProviderResultsReadyCallback resultReadyCallback);
    void removeSuggestionsReceivedCallback(SearchProviderResultsReadyCallback resultReadyCallback);

    ISearchResultViewFactory getSuggestionViewFactory();
}
