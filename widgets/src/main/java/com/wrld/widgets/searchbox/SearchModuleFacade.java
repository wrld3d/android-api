// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

interface SearchModuleFacade {
    void setDefaultSearchResultViewFactory(SearchResultViewFactory factory);

    void addSearchProvider(SearchProvider provider, boolean doSuggestions);

    void addConsumer(SearchResultConsumer consumer);
}
