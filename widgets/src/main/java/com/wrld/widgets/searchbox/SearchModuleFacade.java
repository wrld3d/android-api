// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

interface SearchModuleFacade {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);

    void addSearchProvider(SearchProvider provider);
    void addSearchProvider(SearchProvider provider, SearchResultViewFactory customFactory);

    void addAutoCompleteProvider(AutocompleteProvider provider);
    void addConsumer(SearchResultConsumer consumer);
}
