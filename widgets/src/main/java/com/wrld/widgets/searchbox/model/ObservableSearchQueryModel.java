package com.wrld.widgets.searchbox.model;

public interface ObservableSearchQueryModel {
    void addListener(IOnSearchListener listener);
    void removeListener(IOnSearchListener listener);

}
