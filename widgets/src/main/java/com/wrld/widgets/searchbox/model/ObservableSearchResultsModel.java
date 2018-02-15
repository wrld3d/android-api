package com.wrld.widgets.searchbox.model;

import java.util.List;

public interface ObservableSearchResultsModel {
    List<SearchProviderQueryResult> getCurrentQueryResults();
    int getTotalCurrentQueryResults();

    void addResultListener(IOnSearchResultListener listener);
    void removeResultListener(IOnSearchResultListener listener);
}
