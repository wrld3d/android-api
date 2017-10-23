// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

public interface SearchResultConsumer {
    void onNewSearch(String query);
    void onSearchCancelled();
    void onResultsReturned(SearchResultSet resultSet);
    void onSearchResultClicked(SearchResult result);
    void onSearchResultActivated(SearchResult result);
}
