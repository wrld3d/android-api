package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malcolm.brown on 19/01/2018.
 */


interface SearchQueryListener
{
    void onSearchQueryCompleted(List<SearchProviderQueryResult> results);
    void onSearchQueryCancelled();
}

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

    public SearchQuery(String queryString, Object queryContext, SearchQueryListener listener)
    {
        m_queryString = queryString;
        m_queryContext = queryContext;
        m_listener = listener;
        m_cancelled = false;
        m_inProgress = false;
        m_providerQueries = new ArrayList<SearchProviderQuery>();
        m_results = new ArrayList<SearchProviderQueryResult>();
    }

    public void start()
    {
        // Loop through all SearchProviderQueries, observe and kick them off.
        m_inProgress = true;
        for(SearchProviderQuery providerQuery : m_providerQueries)
        {
            providerQuery.start(this);
        }
    }


    public void cancel()
    {
        // Cancel all inflight queries and notify cancelled.
        m_cancelled = true;
        m_inProgress = false;
        for (SearchProviderQuery query : m_providerQueries)
        {
            if(query.getState() == SearchProviderQueryState.InProgress)
            {
                query.cancel();
            }
        }

        m_listener.onSearchQueryCancelled();
    }

    public void addProvider(MappedSearchProvider provider)
    {
        // TODO: Calling this while in progress is bogus.
        if(m_inProgress)
        {
            return;
        }

        SearchProviderQuery providerQuery = new SearchProviderQuery(provider, this);
        m_providerQueries.add(providerQuery);
    }

    @Override
    public void onSearchProviderQueryCompleted(SearchProviderQueryResult result) {

        if(m_cancelled)
        {
            return;
        }

        m_results.add(result);
        // Check to see who else is still to go.
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

    @Override
    public void onSearchProviderQueryCancelled() {

    }
}
