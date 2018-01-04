package com.wrld.widgets.searchbox.api;

public interface SearchQueryHandler {
    void searchFor(String query);
    void getSuggestionsFor(String text);
    CharSequence getCurrentQuery();
}
