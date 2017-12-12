package com.wrld.widgets.searchbox;

interface SearchQueryHandler {
    void searchFor(String query);
    void getSuggestionsFor(String text);
}
