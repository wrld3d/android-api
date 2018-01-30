package com.wrld.widgets.searchbox.model;

/**
 * Shared base logic for calling and tracking a call to a SearchProvider
 */
public class SearchProviderQueryBase implements ISearchProviderResultsReadyCallback {


    public enum SearchProviderQueryState {
        NotStarted, InProgress, Success, Failed, Cancelled
    }

    protected SearchProviderQueryState m_state;

    private SearchProviderQueryListener m_listener;
    private final int m_providerId;

    public SearchProviderQueryState getState() { return m_state; }

    boolean isCompleted() {
        return  m_state != SearchProviderQueryState.NotStarted &&
                m_state != SearchProviderQueryState.InProgress;
    }

    SearchProviderQueryBase(int providerId)
    {
        m_providerId = providerId;
        m_state = SearchProviderQueryState.NotStarted;
        m_listener = null;
    }

    public void setListener(SearchProviderQueryListener listener)
    {
        m_listener = listener;
    }

    public void start(String queryText, Object queryContext)
    {
        // Internal guard against starting twice
        if(m_state != SearchProviderQueryState.NotStarted)
        {
            // !!
        }
        m_state = SearchProviderQueryState.InProgress;
        doSearch(queryText, queryContext);
    }


    protected void doSearch(String queryText, Object queryContext)
    {
        // override this.
        onQueryCompleted(new ISearchResult[0], true);
    }

    public void onQueryCompleted(ISearchResult[] searchResults, Boolean success)
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
            // Use default search result.
            ISearchResult[] results = new ISearchResult[0];
            m_listener.onSearchProviderQueryCompleted(new SearchProviderQueryResult(m_providerId, results, false));
        }
    }

    public void cancel() {
        if (m_state == SearchProviderQueryState.InProgress)
        {
            onQueryCancelled();
        }
    }
}
