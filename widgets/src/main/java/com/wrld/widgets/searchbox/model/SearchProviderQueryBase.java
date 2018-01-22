package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResult;

/**
 * Created by malcolm.brown on 19/01/2018.
 */

interface SearchProviderQueryListener
{
    void onSearchProviderQueryCompleted(SearchProviderQueryResult result);
    void onSearchProviderQueryCancelled();
}

public class SearchProviderQueryBase {


    public enum SearchProviderQueryState {
        NotStarted, InProgress, Success, Failed, Cancelled
    }

    protected SearchProviderQueryState m_state;

    private final SearchProviderQueryListener m_listener;
    private final int m_providerId;
    private SearchQuery m_query;

    SearchProviderQueryState getState() { return m_state; }

    boolean isCompleted() {
        return  m_state != SearchProviderQueryState.NotStarted &&
                m_state != SearchProviderQueryState.InProgress;
    }

    SearchProviderQueryBase(SearchProviderQueryListener listener, int providerId)
    {
        //m_provider = provider;
        m_providerId = providerId;
        m_listener = listener;
        m_state = SearchProviderQueryState.NotStarted;
    }

    void start(SearchQuery query)
    {
        // Internal guard against starting twice
        if(m_state != SearchProviderQueryState.NotStarted)
        {
            // !!
        }
        m_state = SearchProviderQueryState.InProgress;

        m_query = query;
        doSearch(query);

    }


    public void doSearch(SearchQuery query)
    {
        // m_provider.getSearchProvider().getSearchResults(m_query);
        onQueryCompleted(new SearchResult[0], true);
    }

    public void onQueryCompleted(SearchResult[] searchResults, boolean success)
    {
        m_state = success ? SearchProviderQueryState.Success : SearchProviderQueryState.Failed;

        if(m_listener != null) {
            SearchProviderQueryResult result = new SearchProviderQueryResult(m_providerId, searchResults, success);
            m_listener.onSearchProviderQueryCompleted(result);
        }
    }

    public void onQueryCancelled() {
        m_state = SearchProviderQueryState.Cancelled;
        if(m_listener != null) {
            m_listener.onSearchProviderQueryCancelled();
        }
    }

    void cancel() {
        if (m_state == SearchProviderQueryState.InProgress)
        {
            onQueryCancelled();

            // TODO: Implement cancelling.
            //m_provider.getSearchProvider().cancelCurrentQuery();
        }
    }
}
