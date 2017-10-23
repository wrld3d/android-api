// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

interface SearchResultsContainer {
    void addSearchResultSet(SearchResultSet searchResultSet, SearchResultViewFactory factory);
    void refresh();
}
