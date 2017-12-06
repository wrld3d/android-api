package com.wrld.widgets.searchbox;

public interface SearchResultConsumer {
    void onNewSearch(String query);
    void onSearchCancelled();
    void onResultsReturned(SearchResultSet resultSet);
    void onSearchResultClicked(SearchResult result);
    void onSearchResultActivated(SearchResult result);
}
