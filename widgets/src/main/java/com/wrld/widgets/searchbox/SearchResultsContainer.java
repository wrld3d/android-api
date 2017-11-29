// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

interface SearchResultsContainer {
    void searchStarted();
    void showSuggestions(boolean flag);
    void showResults(boolean flag);
    void refreshContent();
    void setState(ResultSetState state);
}
