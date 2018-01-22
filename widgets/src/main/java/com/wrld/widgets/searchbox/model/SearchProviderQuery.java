package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

/**
 * Created by malcolm.brown on 19/01/2018.
 */

class SearchProviderQuery extends SearchProviderQueryBase implements QueryResultsReadyCallback {

    private MappedSearchProvider m_provider;

    SearchProviderQuery(MappedSearchProvider provider, SearchProviderQueryListener listener)
    {
        super(listener, provider.getId());
        m_provider = provider;

        // TODO: Does this need removing?
        m_provider.getSearchProvider().addSearchCompletedCallback(this);
    }

    @Override
    public void doSearch(SearchQuery query)
    {
        m_provider.getSearchProvider().getSearchResults(query);
    }

    @Override
    void cancel() {
        if (m_state == SearchProviderQueryState.InProgress)
        {
            // TODO: Implement cancelling.
            //m_provider.getSearchProvider().cancelCurrentQuery();
        }
    }
}
