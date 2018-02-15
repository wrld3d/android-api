package com.wrld.widgets.searchbox.model;

public interface ObservableSearchQueryModel {
    void addListener(SearchModelListener listener);
    void removeListener(SearchModelListener listener);

}
