package com.wrld.widgets.searchbox;

interface SearchResultsController {
    SearchResultSet.OnResultChanged getUpdateCallback();
    void refreshContent();
}
