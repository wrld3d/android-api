package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.events.MenuVisibilityChangedCallback;
import com.wrld.widgets.searchbox.api.events.QueryPerformedCallback;
import com.wrld.widgets.searchbox.api.events.QueryCompletedCallback;
import com.wrld.widgets.searchbox.api.events.SearchResultSelectedCallback;

interface SearchModuleFacade {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);
    void setDefaultSuggestionViewFactory(SearchResultViewFactory factory);

    void setSearchProviders(final SearchProvider[] searchProviders);
    void setSuggestionProviders(final SuggestionProvider[] autocompleteSuggestionProviders);

    SearchBoxMenuGroup getGroupByIndex(int index);

    void showDefaultView();
    SearchBoxMenuGroup addGroup(String title);

    void addSearchPerformedCallback(QueryPerformedCallback queryPerformedCallback);
    void removeSearchPerformedCallback(QueryPerformedCallback queryPerformedCallback);

    void addSuggestionPerformedCallback(QueryPerformedCallback queryPerformedCallback);
    void removeSuggestionPerformedCallback(QueryPerformedCallback queryPerformedCallback);

    void addSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback);
    void removeSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback);

    void addMenuVisibilityCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback);
    void removeMenuVisibilityChangedCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback);

    void addSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback);
    void removeSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback);
}
