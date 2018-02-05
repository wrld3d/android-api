package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;


public class SearchQuery implements SearchProviderQueryListener{

    private String m_queryString;
    private Object m_queryContext;
    private QueryType m_queryType;
    private boolean m_cancelled;
    private boolean m_inProgress;
    private final SearchQueryListener m_listener;

    private List<SearchProviderQueryBase> m_providerQueries;
    private List<SearchProviderQueryResult> m_results;

    public final String getQueryString() { return m_queryString; }
    public final Object getQueryContext() { return m_queryContext; }
    public final QueryType getQueryType() { return m_queryType; }

    public enum QueryType {
        Search,
        Suggestion
    }

    public SearchQuery(String queryString,
                       Object queryContext,
                       QueryType queryType,
                       SearchQueryListener listener,
                       List<SearchProviderQueryBase> providerQueries)
    {
        m_queryString = queryString;
        m_queryContext = queryContext;
        m_queryType = queryType;
        m_listener = listener;
        m_cancelled = false;
        m_inProgress = false;
        m_providerQueries = providerQueries;
        m_results = new ArrayList<SearchProviderQueryResult>();
    }

    public void start()
    {
        if(m_providerQueries.size() == 0)
        {
            checkIsComplete();
            return;
        }

        // Loop through all SearchProviderQueries, observe and kick them off.
        m_inProgress = true;
        for(SearchProviderQueryBase providerQuery : m_providerQueries)
        {
            providerQuery.setListener(this);
            providerQuery.start(m_queryString, m_queryContext);
        }
    }


    public void cancel()
    {
        if(m_inProgress && !m_cancelled)
        {
            m_cancelled = true;
            m_inProgress = false;
            for (SearchProviderQueryBase query : m_providerQueries)
            {
                query.cancel();
                query.cleanup();
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
            cleanup();

            m_inProgress = false;
            m_listener.onSearchQueryCompleted(m_results);
        }
    }

    public boolean isComplete() {
        for (SearchProviderQueryBase query : m_providerQueries)
        {
            if(!query.isCompleted())
            {
                return false;
            }
        }
        return true;
    }

    private void cleanup() {
        for (SearchProviderQueryBase query : m_providerQueries)
        {
            query.cleanup();
        }
    }


}
