package com.wrld.widgets.searchbox;

interface SearchModuleFacade {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);

    void addSearchProvider(SearchProvider provider, boolean doSuggestions);

    void addConsumer(SearchResultConsumer consumer);
}
