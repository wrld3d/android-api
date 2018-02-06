package com.wrld.widgets.searchbox.model;

import android.util.Log;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class MappedSearchProvider
{
    MappedSearchProvider(ISearchProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    ISearchProvider getSearchProvider() { return m_searchProvider; }

    private ISearchProvider m_searchProvider;
    private int m_providerId;
}

public class SearchWidgetSearchModel implements SearchQueryListener
{
    private SearchQuery m_currentQuery;
    private List<SearchProviderQueryResult> m_currentQueryResults;
    private Map<Integer, MappedSearchProvider> m_searchProviderMap;
    private IOnSearchListener m_searchListener;
    private IOnSearchResultListener m_resultsListener;

    private int m_nextProviderId = 0;

    public SearchWidgetSearchModel()
    {
        m_searchProviderMap = new HashMap<>();
        m_searchListener = null;
        m_resultsListener = null;
    }

    public void setSearchListener(IOnSearchListener listener)
    {
        m_searchListener = listener;
    }
    public void setResultListener(IOnSearchResultListener listener) { m_resultsListener = listener; }

    public final SearchQuery getCurrentQuery() { return m_currentQuery; }
    public final List<SearchProviderQueryResult> getCurrentQueryResults() { return m_currentQueryResults; }

    public void addSearchProvider(ISearchProvider provider)
    {
        // NOTE: Guard against adding
        int providerId = m_nextProviderId++;
        m_searchProviderMap.put(providerId, new MappedSearchProvider(provider, providerId));
    }

    public void removeSearchProvider(ISearchProvider provider)
    {
        // TODO: Observer clearing any results or queries belong to this provider.

        // TODO: This seems nonsense - cleaner way of doing this.
        if(m_searchProviderMap.containsValue(provider))
        {
            for (Map.Entry<Integer,MappedSearchProvider> entry : m_searchProviderMap.entrySet()) {
                if(entry.getValue().getSearchProvider() == provider) {
                    m_searchProviderMap.remove(entry.getKey());
                    return;
                }
            }
        }
    }



    public void doSearch(String queryText)
    {
        doSearch(queryText, null);
    }

    public void doSearch(String queryText, Object queryContext)
    {
        cancelCurrentQuery();

        SearchQueryListener listener = this;
        m_currentQuery = BuildSearchQuery(queryText, queryContext, listener, m_searchProviderMap);

        // NOTE: Search query hasn't actually started yet, but is about to - this is to avoid issues
        // where queries will complete immediately, and the Started event will occur after Complete
        if(m_searchListener != null) {
            m_searchListener.onSearchQueryStarted(m_currentQuery);
        }

        m_currentQuery.start();

    }

    private SearchQuery BuildSearchQuery(String queryText,
                                         Object queryContext,
                                         SearchQueryListener listener,
                                         Map<Integer, MappedSearchProvider> providerMap)
    {
        List<SearchProviderQueryBase> providerQueries = new ArrayList<>(providerMap.size());
        for(MappedSearchProvider mappedProvider : providerMap.values()) {
            providerQueries.add(new SearchProviderQuery(mappedProvider));
        }
        return new SearchQuery(queryText, queryContext, SearchQuery.QueryType.Search, listener, providerQueries);
    }



    public void cancelCurrentQuery()
    {
        if(m_currentQuery != null) {
            m_currentQuery.cancel();
        }
    }

    public void clear()
    {
        cancelCurrentQuery();

        m_currentQuery = null;
        m_currentQueryResults = null;

        if(m_resultsListener != null) {
            m_resultsListener.onSearchResultsCleared();
        }
    }

    @Override
    public void onSearchQueryCompleted(List<SearchProviderQueryResult> results) {
        m_currentQueryResults = results;
        if(m_resultsListener != null) {
            m_resultsListener.onSearchResultsRecieved(m_currentQuery, m_currentQueryResults);
        }

        if(m_searchListener != null) {
            m_searchListener.onSearchQueryCompleted(m_currentQuery, m_currentQueryResults);
        }
    }

    @Override
    public void onSearchQueryCancelled() {

        if(m_searchListener != null) {
            m_searchListener.onSearchQueryCancelled(m_currentQuery);
        }
    }

    public int getTotalCurrentQueryResults() {
        if(m_currentQueryResults == null) {
            return 0;
        }
        int total = 0;
        for(SearchProviderQueryResult result : m_currentQueryResults) {
            if(result.getResults() != null && result.wasSuccess()) {
                total += result.getResults().length;
            }
        }
        return total;
    }
}
