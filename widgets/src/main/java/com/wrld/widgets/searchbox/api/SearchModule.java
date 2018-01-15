package com.wrld.widgets.searchbox.api;

import android.content.Intent;

import com.wrld.widgets.searchbox.api.events.MenuVisibilityChangedCallback;
import com.wrld.widgets.searchbox.api.events.QueryPerformedCallback;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;
import com.wrld.widgets.searchbox.api.events.SearchResultSelectedCallback;
import com.wrld.widgets.searchbox.menu.SearchBoxMenuGroup;

public interface SearchModule extends SearchQueryHandler {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);
    void setDefaultSuggestionViewFactory(SearchResultViewFactory factory);

    void setSearchProviders(final SearchProvider[] searchProviders);
    void setSuggestionProviders(final SuggestionProvider[] autocompleteSuggestionProviders);

    SearchBoxMenuGroup getGroupByIndex(int index);

    void showDefaultView();
    SearchBoxMenuGroup addGroup(String title);

    boolean handleSearchIntent(Intent intent);

    void doSearch(String search);

    void addSearchPerformedCallback(QueryPerformedCallback queryPerformedCallback);
    void removeSearchPerformedCallback(QueryPerformedCallback queryPerformedCallback);

    void addSuggestionsRequestedCallback(QueryPerformedCallback queryPerformedCallback);
    void removeSuggestionsRequestedCallback(QueryPerformedCallback queryPerformedCallback);

    void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback);
    void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback);

    void addSuggestionsReturnedCallback(QueryResultsReadyCallback queryResultsReadyCallback);
    void removeSuggestionsReturnedCallback(QueryResultsReadyCallback queryResultsReadyCallback);

    void addMenuVisibilityCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback);
    void removeMenuVisibilityChangedCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback);

    void addSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback);
    void removeSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback);

    void addSuggestionSelectedCallback(SearchResultSelectedCallback suggestionSelectedCallback);
    void removeSuggestionSelectedCallback(SearchResultSelectedCallback suggestionSelectedCallback);
}
