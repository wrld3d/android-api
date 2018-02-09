package com.wrld.widgets.searchbox.model;

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
    private SearchResultsModel m_results;
    private Map<Integer, MappedSearchProvider> m_searchProviderMap;
    private IOnSearchListener m_searchListener;


    private int m_nextProviderId = 0;

    public SearchWidgetSearchModel(SearchResultsModel results)
    {
        m_searchProviderMap = new HashMap<>();
        m_searchListener = null;
        m_results = results;
    }

    public void setSearchListener(IOnSearchListener listener)
    {
        m_searchListener = listener;
    }

    public final SearchQuery getCurrentQuery() { return m_currentQuery; }

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
        m_results.clear();
    }

    @Override
    public void onSearchQueryCompleted(List<SearchProviderQueryResult> results) {
        m_results.setResultsForQuery(results, m_currentQuery);

        if(m_searchListener != null) {
            m_searchListener.onSearchQueryCompleted(m_currentQuery, results);
        }
    }

    @Override
    public void onSearchQueryCancelled() {

        if(m_searchListener != null) {
            m_searchListener.onSearchQueryCancelled(m_currentQuery);
        }
    }
    public ISearchResultViewFactory getViewFactoryForProvider(int providerId) {
        if(!m_searchProviderMap.containsKey(providerId)) {
            return null;
        }

        return m_searchProviderMap.get(providerId).getSearchProvider().getResultViewFactory();
    }

    public ISearchProvider getProviderById(int providerId) {
        if(m_searchProviderMap.containsKey(providerId)) {
            return m_searchProviderMap.get(providerId).getSearchProvider();
        }
        return null;
    }
}
