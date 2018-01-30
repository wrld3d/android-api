package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;
import java.util.List;


public class SearchQuery implements SearchProviderQueryListener{

    private String m_queryString;
    private Object m_queryContext;
    private boolean m_cancelled;
    private boolean m_inProgress;
    private final SearchQueryListener m_listener;

    private List<SearchProviderQuery> m_providerQueries;
    private List<SearchProviderQueryResult> m_results;

    public String getQueryString() { return m_queryString; }
    public Object getQueryContext() { return m_queryContext; }

    public SearchQuery(String queryString,
                       Object queryContext,
                       SearchQueryListener listener,
                       List<SearchProviderQuery> providerQueries)
    {
        m_queryString = queryString;
        m_queryContext = queryContext;
        m_listener = listener;
        m_cancelled = false;
        m_inProgress = false;
        m_providerQueries = providerQueries;
        m_results = new ArrayList<SearchProviderQueryResult>();
    }

    public void start()
    {
        // Loop through all SearchProviderQueries, observe and kick them off.
        m_inProgress = true;
        for(SearchProviderQuery providerQuery : m_providerQueries)
        {
            providerQuery.setListener(this);
            providerQuery.start(m_queryString, m_queryContext);
        }
    }


    public void cancel()
    {
        if(m_inProgress && !m_cancelled)
        {
            // Cancel all inflight queries and notify cancelled.
            m_cancelled = true;
            m_inProgress = false;
            for (SearchProviderQuery query : m_providerQueries)
            {
                query.cancel();
            }

            m_listener.onSearchQueryCancelled();
        }
    }

    @Override
    public void onSearchProviderQueryCompleted(SearchProviderQueryResult result) {

        if(m_cancelled)
        {
            return;
        }

        m_results.add(result);
        checkIsComplete();
    }

    private void checkIsComplete() {

        if(isComplete())
        {
            m_inProgress = false;
            // TODO: What is the definition of 'success' for a set of provider queries?
            m_listener.onSearchQueryCompleted(m_results);
        }
    }

    public boolean isComplete() {
        for (SearchProviderQuery query : m_providerQueries)
        {
            if(!query.isCompleted())
            {
                return false;
            }
        }
        return true;
    }


}
