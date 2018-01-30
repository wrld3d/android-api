package com.wrld.widgets.searchbox.model;

import java.util.List;


interface SearchQueryListener
{
    void onSearchQueryCompleted(List<SearchProviderQueryResult> results);
    void onSearchQueryCancelled();
}
