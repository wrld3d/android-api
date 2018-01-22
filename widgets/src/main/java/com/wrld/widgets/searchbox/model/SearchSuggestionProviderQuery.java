package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

/**
 * Created by malcolm.brown on 19/01/2018.
 */

class SearchSuggestionProviderQuery extends SearchProviderQueryBase implements QueryResultsReadyCallback {

    private MappedSearchSuggestionProvider m_provider;

    public SearchSuggestionProviderQuery(MappedSearchSuggestionProvider provider, SearchProviderQueryListener listener) {
        super(listener, provider.getId());
        m_provider = provider;

        // TODO: Does this need removing?
        m_provider.getSuggestionProvider().addSuggestionsReceivedCallback(this);
    }

    @Override
    public void doSearch(SearchQuery query) {
        m_provider.getSuggestionProvider().getSuggestions(query);
    }

    @Override
    void cancel() {
        if (m_state == SearchProviderQueryState.InProgress) {
            // TODO: Implement cancelling.
            //m_provider.getSearchProvider().cancelCurrentQuery();
        }
    }

}