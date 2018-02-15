package com.wrld.widgets.searchbox.model;

public interface ObservableSuggestionQueryModel {
    void addListener(SuggestionQueryModelListener listener);
    void removeListener(SuggestionQueryModelListener listener);

}
