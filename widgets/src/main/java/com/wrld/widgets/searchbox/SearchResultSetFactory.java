package com.wrld.widgets.searchbox;

class SearchResultSetFactory {
    public SearchResultSet createResultSetForSearchProvider(SearchProvider searchProvider){
        SearchResultSet searchResultSet = new SearchResultSet();
        searchProvider.addSearchCompletedCallback(searchResultSet.getUpdateCallback());
        searchResultSet.createSearchProviderDeregistrationCallback(searchProvider);
        return searchResultSet;
    }

    public SearchResultSet createResultSetForSuggestionProvider(SuggestionProvider suggestionProvider){
        SearchResultSet suggestionSet = new SearchResultSet();
        suggestionProvider.addSuggestionsReceivedCallback(suggestionSet.getUpdateCallback());
        suggestionSet.createSuggestionProviderDeregistrationCallback(suggestionProvider);
        return suggestionSet;
    }
}
