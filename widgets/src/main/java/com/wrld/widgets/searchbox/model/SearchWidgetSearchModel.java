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

class MappedSuggestionProvider
{
    MappedSuggestionProvider(ISuggestionProvider provider, int id)
    {
        m_providerId = id;
        m_searchProvider = provider;
    }

    public int getId() { return m_providerId; }
    ISuggestionProvider getSuggestionProvider() { return m_searchProvider; }

    private ISuggestionProvider m_searchProvider;
    private int m_providerId;
}


public class SearchWidgetSearchModel implements SearchQueryListener
{
    private SearchQuery m_currentQuery;
    private List<SearchProviderQueryResult> m_currentQueryResults;
    private Map<Integer, MappedSearchProvider> m_searchProviderMap;
    private Map<Integer, MappedSuggestionProvider> m_suggestionProviderMap;
    private ISearchWidgetModelListener m_listener;

    private int m_nextProviderId = 0;

    public SearchWidgetSearchModel()
    {
        m_searchProviderMap = new HashMap<>();
        m_suggestionProviderMap = new HashMap<>();
        m_listener = null;
    }

    public void setListener(ISearchWidgetModelListener listener)
    {
        m_listener = listener;
    }

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

        // NOTE: Search query hasn't actually started yet, but is about to - this is to avoid issues
        // where queries will complete immediately, and the Started event will occur after Complete
        if(m_listener != null) {
            m_listener.onSearchQueryStarted(m_currentQuery);
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

    private SearchQuery BuildSuggestionQuery(String queryText,
                                             Object queryContext,
                                             SearchQueryListener listener,
                                             Map<Integer, MappedSuggestionProvider> providerMap)
    {
        List<SearchProviderQueryBase> providerQueries = new ArrayList<>(providerMap.size());
        for(MappedSuggestionProvider mappedProvider : providerMap.values()) {
            providerQueries.add(new SearchSuggestionProviderQuery(mappedProvider));
        }
        return new SearchQuery(queryText, queryContext, SearchQuery.QueryType.Suggestion, listener, providerQueries);
    }

    public void doSuggestions(String queryText) {
        cancelCurrentQuery();

        SearchQueryListener listener = this;
        m_currentQuery = BuildSuggestionQuery(queryText, null, listener, m_suggestionProviderMap);

        if(m_listener != null) {
            m_listener.onSuggestionQueryStarted(m_currentQuery);
        }

        m_currentQuery.start();
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

        if(m_listener != null) {
            m_listener.onSearchResultsCleared();
        }
    }

    @Override
    public void onSearchQueryCompleted(List<SearchProviderQueryResult> results) {
        m_currentQueryResults = results;
        Log.d("EEGEO", "You got " + results.size() + " results via " + m_currentQuery.getQueryType());
        if(m_listener != null) {
            if(m_currentQuery.getQueryType() == SearchQuery.QueryType.Search) {
                m_listener.onSearchQueryCompleted(m_currentQuery, m_currentQueryResults);
            }
            else {
                m_listener.onSuggestionQueryCompleted(m_currentQuery, m_currentQueryResults);
            }
        }
    }

    @Override
    public void onSearchQueryCancelled() {

        if(m_listener != null) {
            m_listener.onSearchQueryCancelled();
        }
    }

    public int getSuggestionProviderCount() {
        return m_suggestionProviderMap.size();
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

    // Shouldn't have this - move providers to external repository and expose bits with interfaces.
    public ISearchResultViewFactory getViewFactoryForProvider(int providerId) {
         if(!m_suggestionProviderMap.containsKey(providerId))  {
             return null;
         }

        return m_suggestionProviderMap.get(providerId).getSuggestionProvider().getSuggestionViewFactory();
    }
}
