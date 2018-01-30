package com.wrld.widgets.searchbox.model;

/**
 * Listener for a SearchProviderQuery being completed or cancelled
 */
interface SearchProviderQueryListener
{
    void onSearchProviderQueryCompleted(SearchProviderQueryResult result);
}
