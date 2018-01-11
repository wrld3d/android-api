package com.wrld.widgets.searchbox;

import android.content.Context;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SuggestionProvider;

class SearchResultSetFactory {

    private int m_suggestionsDisplayed = 3;
    private int m_resultsDisplayed = 3;

    public SearchResultSetFactory(int numSuggestions, int numResults){
        m_suggestionsDisplayed = numSuggestions;
        m_resultsDisplayed = numResults;
    }

    public SearchResultSet createResultSetForSearchProvider(SearchProvider searchProvider){
        SearchResultSet searchResultSet = new SearchResultSet(m_resultsDisplayed);
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
