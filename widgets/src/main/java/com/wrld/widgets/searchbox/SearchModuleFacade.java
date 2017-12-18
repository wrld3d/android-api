package com.wrld.widgets.searchbox;

interface SearchModuleFacade {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);
    void setDefaultSuggestionViewFactory(SearchResultViewFactory factory);

    void setSearchProviders(final SearchProvider[] searchProviders);
    void setSuggestionProviders(final SuggestionProvider[] autocompleteSuggestionProviders);

    SearchBoxMenuGroup getGroupByIndex(int index);

    void showDefaultView();
    SearchBoxMenuGroup addGroup(String title);

    //TODO void addConsumer(SearchResultConsumer consumer);
}
