package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SuggestionProvider;

class SearchResultSetFactory {

    private int m_suggestionsDisplayed = 3;

    public SearchResultSet createResultSetForSearchProvider(SearchProvider searchProvider){
        SearchResultSet searchResultSet = new SearchResultSet();
        searchProvider.addSearchCompletedCallback(searchResultSet.getUpdateCallback());
        searchResultSet.createSearchProviderDeregistrationCallback(searchProvider);
        return searchResultSet;
    }

    public SearchResultSet createResultSetForSuggestionProvider(SuggestionProvider suggestionProvider){
        SearchResultSet suggestionSet = new SearchResultSet(m_suggestionsDisplayed);
        suggestionProvider.addSuggestionsReceivedCallback(suggestionSet.getUpdateCallback());
        suggestionSet.createSuggestionProviderDeregistrationCallback(suggestionProvider);
        return suggestionSet;
    }
}
