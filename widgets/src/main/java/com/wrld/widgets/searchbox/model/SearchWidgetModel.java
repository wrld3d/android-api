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
    public MappedSearchProvider(SearchProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    public SearchProvider getSearchProvider() { return m_searchProvider; }

    private SearchProvider m_searchProvider;
    public int m_providerId;
}

class MappedSuggestionProvider
{
    public MappedSuggestionProvider(SuggestionProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    public SuggestionProvider getSuggestionProvider() { return m_searchProvider; }

    private SuggestionProvider m_searchProvider;
    public int m_providerId;
}

public class SearchWidgetModel implements SearchQueryListener
{

    SearchQuery m_currentQuery;
    List<SearchProviderQueryResult> m_currentQueryResults;
    Map<Integer, MappedSearchProvider> m_searchProviderMap;
    Map<Integer, MappedSuggestionProvider> m_suggestionProviderMap;



    int m_nextProviderId = 0;

    public SearchWidgetModel()
    {
        m_searchProviderMap = new HashMap();
    }

    // Add providers.
    public void addSearchProvider(SearchProvider provider)
    {
        // NOTE: Guard against adding
        int providerId = m_nextProviderId++;
        m_searchProviderMap.put(providerId, new MappedSearchProvider(provider, providerId));
    }

    public void removeSearchProvider(SearchProvider provider)
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

    public void addSuggestionProvider(SuggestionProvider provider)
    {
        // NOTE: Guard against adding
        int providerId = m_nextProviderId++;
        m_searchProviderMap.put(providerId, new MappedSearchProvider(provider, providerId));
    }

    public void removeSuggestionProvider(SuggestionProvider provider)
    {
        // TODO: Observer clearing any results or queries belong to this provider.

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
        // cancel existing.
        if(m_currentQuery != null)
        {
            m_currentQuery.cancel();
            m_currentQuery = null;
        }

        m_currentQuery = new SearchQuery(queryText, queryContext, this);
        for(MappedSearchProvider mappedProvider : m_searchProviderMap.values()) {
            // TODO: Change this to add a ProviderQuery of either type (Search/Suggestions)
            // (And then factor this out do a general 'doQuery'?)
            m_currentQuery.addProvider(mappedProvider);
        }

        m_currentQuery.start();
    }

    public void doSuggestions(String queryText) {

    }

    public void clear()
    {
        m_currentQuery = null;
        m_currentQueryResults = null;
        //m_listener.notifyQueryCleared();
    }

    @Override
    public void onSearchQueryCompleted(List<SearchProviderQueryResult> results) {
        // Populate results view model.
        m_currentQueryResults = results;
        //m_listener.notifyQueryCompleted(m_currentQuery, m_currentQueryResults);
    }

    @Override
    public void onSearchQueryCancelled() {
       // m_listener.notifyQueryCancelled();
    }
}
