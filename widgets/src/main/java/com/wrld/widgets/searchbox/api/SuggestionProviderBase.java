package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;

public abstract class SuggestionProviderBase extends SearchProviderBase implements SuggestionProvider {
    private SearchResultViewFactory m_suggestionViewFactory;
    private ArrayList<QueryResultsReadyCallback> m_suggestionsReceivedCallbacks;

    public SuggestionProviderBase(String title) {
        super(title);
        m_suggestionsReceivedCallbacks = new ArrayList<QueryResultsReadyCallback>();
    }

    @Override
    public void addSuggestionsReceivedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_suggestionsReceivedCallbacks.add(queryResultsReadyCallback);
    }

    @Override
    public void removeSuggestionsReceivedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_suggestionsReceivedCallbacks.remove(queryResultsReadyCallback);
    }

    protected void performSuggestionCompletedCallbacks(SearchResult[] results){
        QueryResultsReadyCallback[] queryResultsReadyCallbacks = new QueryResultsReadyCallback[m_suggestionsReceivedCallbacks.size()];
        m_suggestionsReceivedCallbacks.toArray(queryResultsReadyCallbacks);
        for(QueryResultsReadyCallback queryResultsReadyCallback : queryResultsReadyCallbacks) {
            queryResultsReadyCallback.onQueryCompleted(results);
        }
    }

    @Override
    public void setSuggestionViewFactory(SearchResultViewFactory factory){
        m_suggestionViewFactory = factory;
    }

    @Override
    public SearchResultViewFactory getSuggestionViewFactory() {
        return m_suggestionViewFactory;
    }
}
