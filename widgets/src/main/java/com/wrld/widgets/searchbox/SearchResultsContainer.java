package com.wrld.widgets.searchbox;

interface SearchResultsContainer {
    void searchStarted();
    void setSuggestionsVisibility(boolean suggestionsVisible);
    void setResultsVisibility(boolean resultsVisible);
    void refreshContent();
    void setResultsViewState(ResultsViewState state);
}
