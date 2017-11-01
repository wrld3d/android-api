// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

interface SearchResultSet {
    SearchResult[] sortOn(String propertyKey);
    SearchResult[] getAllResults();
    SearchResult getResult(int index);

    int getResultCount();

    void addResult(SearchResult result);
    void removeResult(SearchResult result);
    void removeResult(int resultIndex);
    void clear();

    void addOnResultChangedHandler(OnResultChanged callback);

    interface OnResultChanged{
        void invoke();
    }


    // Phase 2
    SearchResult[] getResultsInRange(int min, int max);	// if not enough results in range only returns available
}
