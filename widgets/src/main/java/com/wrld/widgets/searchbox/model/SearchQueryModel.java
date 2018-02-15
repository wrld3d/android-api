package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class MappedSearchProvider
{
    MappedSearchProvider(SearchProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    SearchProvider getSearchProvider() { return m_searchProvider; }

    private SearchProvider m_searchProvider;
    private int m_providerId;
}

public class SearchQueryModel implements SearchQueryListener, ObservableSearchQueryModel
{
    private SearchQuery m_currentQuery;
    private SearchResultsModel m_results;
    private Map<Integer, MappedSearchProvider> m_searchProviderMap;
    private List<SearchQueryModelListener> m_searchListeners;


    private int m_nextProviderId = 0;

    public SearchQueryModel(SearchResultsModel results)
    {
        m_searchProviderMap = new HashMap<>();
        m_searchListeners = new ArrayList<>();
        m_results = results;
    }

    public void addListener(SearchQueryModelListener listener) {
        m_searchListeners.add(listener);
    }

    public void removeListener(SearchQueryModelListener listener) {
        m_searchListeners.remove(listener);
    }

    public final SearchQuery getCurrentQuery() { return m_currentQuery; }

    public void addSearchProvider(SearchProvider provider)
    {
        if(findProvider(provider) == null) {
            int providerId = m_nextProviderId++;
            m_searchProviderMap.put(providerId, new MappedSearchProvider(provider, providerId));
        }
    }

    public void removeSearchProvider(SearchProvider provider)
    {
        MappedSearchProvider mappedProvider = findProvider(provider);
        if(mappedProvider != null) {

            clear();
            m_searchProviderMap.remove(mappedProvider.getId());
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
        for(SearchQueryModelListener searchListener : m_searchListeners) {
            searchListener.onSearchQueryStarted(m_currentQuery);
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

        for(SearchQueryModelListener searchListener : m_searchListeners) {
            searchListener.onSearchQueryCompleted(m_currentQuery, results);
        }
    }

    @Override
    public void onSearchQueryCancelled() {

        for(SearchQueryModelListener searchListener : m_searchListeners) {
            searchListener.onSearchQueryCancelled(m_currentQuery);
        }
    }

    public ISearchResultViewFactory getViewFactoryForProvider(int providerId) {
        if(!m_searchProviderMap.containsKey(providerId)) {
            return null;
        }

        return m_searchProviderMap.get(providerId).getSearchProvider().getResultViewFactory();
    }

    public SearchProvider getProviderById(int providerId) {
        if(m_searchProviderMap.containsKey(providerId)) {
            return m_searchProviderMap.get(providerId).getSearchProvider();
        }
        return null;
    }

    private MappedSearchProvider findProvider(SearchProvider provider) {
        for(Map.Entry<Integer,MappedSearchProvider> entry : m_searchProviderMap.entrySet()) {
            if(entry.getValue().getSearchProvider() == provider) {
                return entry.getValue();
            }
        }
        return null;
    }
}
