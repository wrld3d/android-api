package com.wrld.widgets.searchbox.model;

/**
 * A result from an individual SearchProviderQuery. Contains the results (if any), information on if it
 * was successful, and an id to map it back to the original provider.
 */
public class SearchProviderQueryResult
{
    public SearchProviderQueryResult(int providerId, ISearchResult[] results, boolean success)
    {
        m_providerId = providerId;
        m_results = results;
        m_success = success;
    }

    public final ISearchResult[] getResults() { return m_results; }
    public final int getProviderId() { return m_providerId; }
    public final boolean wasSuccess() { return m_success; }

    private ISearchResult[] m_results;
    private int m_providerId;
    private final boolean m_success;
}