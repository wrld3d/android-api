package com.wrld.widgets.searchbox.model;

public interface ObservableSuggestionQueryModel {
    void addListener(SuggestionModelListener listener);
    void removeListener(SuggestionModelListener listener);

}
