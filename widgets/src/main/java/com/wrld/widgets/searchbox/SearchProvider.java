package com.wrld.widgets.searchbox;

public interface SearchProvider {
    String getTitle();
    void getSearchResults(String query);

    void addOnResultsReceivedCallback(OnResultsReceivedCallback callback);
    void removeOnResultsReceivedCallback(OnResultsReceivedCallback callback);

    void setResultViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getResultViewFactory();
}
