package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.events.QueryCompletedCallback;

import java.util.ArrayList;

public abstract class SuggestionProviderBase extends SearchProviderBase implements SuggestionProvider {
    private SearchResultViewFactory m_suggestionViewFactory;
    private ArrayList<QueryCompletedCallback> m_suggestionsReceivedCallbacks;

    public SuggestionProviderBase(String title) {
        super(title);
        m_suggestionsReceivedCallbacks = new ArrayList<QueryCompletedCallback>();
    }

    @Override
    public void addSuggestionsReceivedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_suggestionsReceivedCallbacks.add(queryCompletedCallback);
    }

    @Override
    public void removeSuggestionsReceivedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_suggestionsReceivedCallbacks.remove(queryCompletedCallback);
    }

    protected void performSuggestionCompletedCallbacks(SearchResult[] results){
        for(QueryCompletedCallback queryCompletedCallback : m_suggestionsReceivedCallbacks) {
            queryCompletedCallback.onQueryCompleted(results);
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
