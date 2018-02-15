package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

// TODO: Factor commonality between these two search models.
public class SearchWidgetSuggestionModel implements SearchQueryListener, ObservableSuggestionQueryModel
{
    private SearchQuery m_currentQuery;
    private SearchResultsModel m_results;
    private Map<Integer, MappedSuggestionProvider> m_suggestionProviderMap;

    private List<IOnSuggestionListener> m_suggestionListeners;

    private int m_nextProviderId = 0;

    public SearchWidgetSuggestionModel(SearchResultsModel resultsModel)
    {
        m_suggestionProviderMap = new HashMap<>();
        m_suggestionListeners = new ArrayList<>();
        m_results = resultsModel;
    }

    public void addListener(IOnSuggestionListener listener) {
        m_suggestionListeners.add(listener);
    }

    public void removeListener(IOnSuggestionListener listener) {
        m_suggestionListeners.remove(listener);
    }

    public final SearchQuery getCurrentQuery() { return m_currentQuery; }

    public void addSuggestionProvider(ISuggestionProvider provider)
    {
        if(findProvider(provider) == null) {
            int providerId = m_nextProviderId++;
            m_suggestionProviderMap.put(providerId, new MappedSuggestionProvider(provider, providerId));
        }
    }

    public void removeSuggestionProvider(ISuggestionProvider provider)
    {
        MappedSuggestionProvider mappedProvider = findProvider(provider);
        if(mappedProvider != null) {

            clear();
            m_suggestionProviderMap.remove(mappedProvider.getId());
        }
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

        for(IOnSuggestionListener suggestionListener : m_suggestionListeners) {
            suggestionListener.onSuggestionQueryStarted(m_currentQuery);
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
        m_results.clear();
    }

    @Override
    public void onSearchQueryCompleted(List<SearchProviderQueryResult> results) {
        m_results.setResultsForQuery(results, m_currentQuery);

        for(IOnSuggestionListener suggestionListener : m_suggestionListeners) {
            suggestionListener.onSuggestionQueryCompleted(m_currentQuery, results);
        }
    }

    @Override
    public void onSearchQueryCancelled() {

        for(IOnSuggestionListener suggestionListener : m_suggestionListeners) {
            suggestionListener.onSuggestionQueryCancelled(m_currentQuery);
        }
    }

    public int getSuggestionProviderCount() {
        return m_suggestionProviderMap.size();
    }

    // Shouldn't have this - move providers to external repository and expose bits with interfaces.
    public ISearchResultViewFactory getViewFactoryForProvider(int providerId) {
        if(!m_suggestionProviderMap.containsKey(providerId))  {
            return null;
        }

        return m_suggestionProviderMap.get(providerId).getSuggestionProvider().getSuggestionViewFactory();
    }

    private MappedSuggestionProvider findProvider(ISuggestionProvider provider) {
        for(Map.Entry<Integer,MappedSuggestionProvider> entry : m_suggestionProviderMap.entrySet()) {
            if(entry.getValue().getSuggestionProvider() == provider) {
                return entry.getValue();
            }
        }
        return null;
    }
}
