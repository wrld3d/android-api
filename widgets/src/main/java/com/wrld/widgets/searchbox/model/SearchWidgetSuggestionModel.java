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
public class SearchWidgetSuggestionModel implements SearchQueryListener
{
    private SearchQuery m_currentQuery;
    private List<SearchProviderQueryResult> m_currentQueryResults;
    private Map<Integer, MappedSuggestionProvider> m_suggestionProviderMap;

    // separate results model and make it observable
    private IOnSuggestionListener m_suggestionListener;
    private IOnSearchResultListener m_resultsListener;

    private int m_nextProviderId = 0;

    public SearchWidgetSuggestionModel()
    {
        m_suggestionProviderMap = new HashMap<>();
        m_suggestionListener = null;
        m_resultsListener = null;
    }

    public void setSuggestionListener(IOnSuggestionListener listener) { m_suggestionListener = listener; }
    public void setResultListener(IOnSearchResultListener listener) { m_resultsListener = listener; }

    public final SearchQuery getCurrentQuery() { return m_currentQuery; }
    public final List<SearchProviderQueryResult> getCurrentQueryResults() { return m_currentQueryResults; }

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

        if(m_suggestionListener != null) {
            m_suggestionListener.onSuggestionQueryStarted(m_currentQuery);
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

        if(m_suggestionListener != null) {
            m_suggestionListener.onSuggestionQueryCompleted(m_currentQuery, m_currentQueryResults);
        }
    }

    @Override
    public void onSearchQueryCancelled() {

        if(m_suggestionListener != null) {
            m_suggestionListener.onSuggestionQueryCancelled(m_currentQuery);
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
