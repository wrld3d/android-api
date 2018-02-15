package com.wrld.widgets.searchbox.model;

import android.support.annotation.NonNull;

/**
 * A result from an individual SearchProviderQuery. Contains the results (if any), information on if it
 * was successful, and an id to map it back to the original provider.
 */
public class SearchProviderQueryResult implements Comparable<SearchProviderQueryResult>
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

    private SearchResult[] m_results;
    private int m_providerId;
    private final boolean m_success;

    @Override
    public int compareTo(@NonNull SearchProviderQueryResult other) {
        return m_providerId > other.getProviderId() ? 1 : (m_providerId < other.getProviderId() ? -1 : 0);
    }
}