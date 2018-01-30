package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResult;

/**
 * A result from an individual SearchProviderQuery. Contains the results (if any), information on if it
 * was successful, and an id to map it back to the original provider.
 */
class SearchProviderQueryResult
{
    public SearchProviderQueryResult(int providerId, SearchResult[] results, boolean success)
    {
        m_providerId = providerId;
        m_results = results;
        m_success = success;
    }

    public final SearchResult[] getResults() { return m_results; }
    public final int getProviderId() { return m_providerId; }
    public final boolean wasSuccess() { return m_success; }

    SearchResult[] m_results;
    int m_providerId;
    private final boolean m_success;
}