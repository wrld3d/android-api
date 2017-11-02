// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

public interface SearchProvider {
    void getSearchResults(String query, OnResultsReceivedCallback callback);
}
