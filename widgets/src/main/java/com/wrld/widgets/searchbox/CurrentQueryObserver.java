package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;

class CurrentQueryObserver {
    private Query m_currentQuery;

    private ArrayList<RemoveCallbackFromProvider> m_searchProviderCallbackDeregistrations;
    private ArrayList<RemoveCallbackFromProvider> m_suggestionProviderCallbackDeregistrations;

    private QueryResultsReadyCallback m_onAllResultsReturned;
    private QueryResultsReadyCallback m_onAllSuggestionsReturned;

    private ArrayList<QueryResultsReadyCallback> m_searchCompletedCallbacks;
    private ArrayList<QueryResultsReadyCallback> m_suggestionsReturnedCallbacks;

    public QueryResultsReadyCallback onAllResultsReturned() { return m_onAllResultsReturned; }
    public QueryResultsReadyCallback onAllSuggestionsReturned() { return m_onAllSuggestionsReturned; }

    private interface RemoveCallbackFromProvider{
        void removeFromProvider();
    }

    public CurrentQueryObserver(){
        m_onAllResultsReturned = createOnAllResultsReturnedCallback();
        m_onAllSuggestionsReturned = createOnAllSuggestionsReturnedCallback();

        m_searchCompletedCallbacks = new ArrayList<QueryResultsReadyCallback>();
        m_suggestionsReturnedCallbacks = new ArrayList<QueryResultsReadyCallback>();

        m_searchProviderCallbackDeregistrations = new ArrayList<RemoveCallbackFromProvider>();
        m_suggestionProviderCallbackDeregistrations = new ArrayList<RemoveCallbackFromProvider>();
    }

    public void setCurrentQuery(Query currentQuery){
        m_currentQuery = currentQuery;
    }

    public void registerWithSearchProviders(SearchProvider[] searchProviders){
        for(RemoveCallbackFromProvider callback : m_searchProviderCallbackDeregistrations){
            callback.removeFromProvider();
        }

        m_searchProviderCallbackDeregistrations.clear();

        for(int i = 0; i < searchProviders.length; ++i){
            final SearchProvider provider = searchProviders[i];
            final QueryResultsReadyCallback providerCompletionCallback = addResultsCallback(i);
            provider.addSearchCompletedCallback(providerCompletionCallback);
            m_searchProviderCallbackDeregistrations.add(new RemoveCallbackFromProvider() {
                @Override
                public void removeFromProvider() {
                    provider.removeSearchCompletedCallback(providerCompletionCallback);
                }
            });
        }
    }

    public void registerWithSuggestionProviders(SuggestionProvider[] suggestionProviders){

        for(RemoveCallbackFromProvider callback : m_suggestionProviderCallbackDeregistrations){
            callback.removeFromProvider();
        }

        m_suggestionProviderCallbackDeregistrations.clear();

        for(int i = 0; i < suggestionProviders.length; ++i){
            final SuggestionProvider provider = suggestionProviders[i];
            final QueryResultsReadyCallback providerCompletionCallback = addResultsCallback(i);
            provider.addSuggestionsReceivedCallback(providerCompletionCallback);
            m_suggestionProviderCallbackDeregistrations.add(new RemoveCallbackFromProvider() {
                @Override
                public void removeFromProvider() {
                    provider.removeSuggestionsReceivedCallback(providerCompletionCallback);
                }
            });
        }
    }

    public void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_searchCompletedCallbacks.add(queryResultsReadyCallback);
    }

    public void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_searchCompletedCallbacks.remove(queryResultsReadyCallback);
    }

    public void addSuggestionsReturnedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_suggestionsReturnedCallbacks.add(queryResultsReadyCallback);
    }

    public void removeSuggestionsReturnedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_suggestionsReturnedCallbacks.remove(queryResultsReadyCallback);
    }

    private QueryResultsReadyCallback addResultsCallback(final int providerIndex){
        return new QueryResultsReadyCallback(){
            @Override
            public void onQueryCompleted(SearchResult[] returnedResults) {
                m_currentQuery.addResults(providerIndex, returnedResults);
            }
        };
    }

    private QueryResultsReadyCallback createOnAllResultsReturnedCallback(){
        return new QueryResultsReadyCallback() {
            @Override
            public void onQueryCompleted(final SearchResult[] allResults) {
                QueryResultsReadyCallback[] callbacks = new QueryResultsReadyCallback[m_searchCompletedCallbacks.size()];
                m_searchCompletedCallbacks.toArray(callbacks);
                for(QueryResultsReadyCallback callback : callbacks){
                    callback.onQueryCompleted(allResults);
                }
            }
        };
    }

    private QueryResultsReadyCallback createOnAllSuggestionsReturnedCallback(){
        return new QueryResultsReadyCallback() {
            @Override
            public void onQueryCompleted(final SearchResult[] allSuggestions) {
                QueryResultsReadyCallback[] callbacks = new QueryResultsReadyCallback[m_suggestionsReturnedCallbacks.size()];
                m_suggestionsReturnedCallbacks.toArray(callbacks);
                for(QueryResultsReadyCallback callback : callbacks){
                    callback.onQueryCompleted(allSuggestions);
                }
            }
        };
    }
}
