package com.wrld.widgets.searchbox.model;

public interface ObservableSuggestionQueryModel {
    void addListener(IOnSuggestionListener listener);
    void removeListener(IOnSuggestionListener listener);

}
