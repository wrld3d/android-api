package com.wrld.widgets.searchbox;

interface SearchModuleFacade {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);
    void setDefaultSuggestionViewFactory(SearchResultViewFactory factory);

    void setSearchProviders(final SearchProvider[] searchProviders);
    void setSuggestionProviders(final SuggestionProvider[] autocompleteSuggestionProviders);

    //TODO void addConsumer(SearchResultConsumer consumer);
}
