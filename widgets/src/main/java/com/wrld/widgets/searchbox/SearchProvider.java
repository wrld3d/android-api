// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

public interface SearchProvider {
    String getTitle();
    void getSearchResults(String query);

    void addOnResultsRecievedCallback(OnResultsReceivedCallback callback);

    void setResultViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getResultViewFactory();
}
