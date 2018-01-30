package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

public interface ISuggestionProvider {
    String getSuggestionTitleFormatting();
    void getSuggestions(String queryText, Object queryContext);
    void cancelSuggestions();
    void addSuggestionsReceivedCallback(ISearchProviderResultsReadyCallback resultReadyCallback);
    void removeSuggestionsReceivedCallback(ISearchProviderResultsReadyCallback resultReadyCallback);

    ISearchResultViewFactory getSuggestionViewFactory();
}
