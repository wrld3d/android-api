package com.wrld.widgets.searchbox;

class SearchResultSetFactory {
    public SearchResultSet createResultSetForSearchProvider(SearchProvider searchProvider){
        SearchResultSet searchResultSet = new SearchResultSet();
        searchProvider.addOnResultsReceivedCallback(searchResultSet.getUpdateCallback());
        searchResultSet.createSearchProviderDeregistrationCallback(searchProvider);
        return searchResultSet;
    }

    public SearchResultSet createResultSetForSuggestionProvider(SuggestionProvider suggestionProvider){
        SearchResultSet searchResultSet = new SearchResultSet();
        suggestionProvider.addOnSuggestionsReceivedCallback(searchResultSet.getUpdateCallback());
        searchResultSet.createSuggestionProviderDeregistrationCallback(suggestionProvider);
        return searchResultSet;
    }
}
