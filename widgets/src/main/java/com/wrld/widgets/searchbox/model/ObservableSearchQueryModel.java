package com.wrld.widgets.searchbox.model;

public interface ObservableSearchQueryModel {
    void addListener(SearchQueryModelListener listener);
    void removeListener(SearchQueryModelListener listener);

}
