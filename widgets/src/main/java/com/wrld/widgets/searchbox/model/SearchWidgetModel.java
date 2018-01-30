package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class MappedSearchProvider
{
    public MappedSearchProvider(ISearchProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    public ISearchProvider getSearchProvider() { return m_searchProvider; }

    private ISearchProvider m_searchProvider;
    public int m_providerId;
}

class MappedSuggestionProvider
{
    public MappedSuggestionProvider(ISuggestionProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    public ISuggestionProvider getSuggestionProvider() { return m_searchProvider; }

    private ISuggestionProvider m_searchProvider;
    public int m_providerId;
}

public class SearchWidgetModel implements SearchQueryListener
{

    private SearchQuery m_currentQuery;
    private List<SearchProviderQueryResult> m_currentQueryResults;
    private Map<Integer, MappedSearchProvider> m_searchProviderMap;
    private Map<Integer, MappedSuggestionProvider> m_suggestionProviderMap;

    private int m_nextProviderId = 0;

    public SearchWidgetModel()
    {
        m_searchProviderMap = new HashMap<>();
        m_suggestionProviderMap = new HashMap<>();
    }

    // Add providers.
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

    public void addSuggestionProvider(ISuggestionProvider provider)
    {
        // NOTE: Guard against adding
        int providerId = m_nextProviderId++;
        m_suggestionProviderMap.put(providerId, new MappedSuggestionProvider(provider, providerId));
    }

    public void removeSuggestionProvider(ISuggestionProvider provider)
    {
        // TODO: Observer clearing any results or queries belong to this provider.
        // Might be easier to cancel current query, remove, and re-run the query

        // TODO: This seems nonsense - cleaner way of doing this.
        if(m_suggestionProviderMap.containsValue(provider))
        {
            for (Map.Entry<Integer,MappedSuggestionProvider> entry : m_suggestionProviderMap.entrySet()) {
                if(entry.getValue().getSuggestionProvider() == provider) {
                    m_suggestionProviderMap.remove(entry.getKey());
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
        m_currentQuery.start();
    }

    private SearchQuery BuildSearchQuery(String queryText,
                                         Object queryContext,
                                         SearchQueryListener listener,
                                         Map<Integer, MappedSearchProvider> providerMap)
    {
        List<SearchProviderQuery> providerQueries = new ArrayList<SearchProviderQuery>(providerMap.size());
        for(MappedSearchProvider mappedProvider : m_searchProviderMap.values()) {
            providerQueries.add(new SearchProviderQuery(mappedProvider));
        }
        return new SearchQuery(queryText, queryContext, listener, providerQueries);
    }

    public void doSuggestions(String queryText) {

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
        //m_listener.notifyQueryCleared();
    }

    @Override
    public void onSearchQueryCompleted(List<SearchProviderQueryResult> results) {
        m_currentQueryResults = results;
        //m_listener.onSearchQueryCompleted(m_currentQuery, m_currentQueryResults);
    }

    @Override
    public void onSearchQueryCancelled() {
       // m_listener.onSearchQueryCancelled();
    }
}
