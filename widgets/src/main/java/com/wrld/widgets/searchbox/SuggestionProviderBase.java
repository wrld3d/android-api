// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import java.util.ArrayList;

public abstract class SuggestionProviderBase extends SearchProviderBase implements SuggestionProvider {
    private SearchResultViewFactory m_suggestionViewFactory;
    private ArrayList<OnResultsReceivedCallback> m_onSuggestionsReceivedCallback;

    public SuggestionProviderBase(String title) {
        super(title);
        m_onSuggestionsReceivedCallback = new ArrayList<OnResultsReceivedCallback>();
    }

    @Override
    public void addOnSuggestionsRecievedCallback(OnResultsReceivedCallback callback) {
        m_onSuggestionsReceivedCallback.add(callback);
    }

    protected void performSuggestionCompletedCallbacks(SearchResult[] results){
        for(OnResultsReceivedCallback callback: m_onSuggestionsReceivedCallback) {
            callback.onResultsReceived(results);
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
