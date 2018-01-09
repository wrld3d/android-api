package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;

class SearchResultSet {

    public enum ExpansionState { HIDDEN, COLLAPSED, EXPANDED };

    interface OnResultChanged{
        void invoke();
    }

    private interface DeregisterCallback {
        void deregister();
    }

    private ArrayList<SearchResult> m_results;
    private ArrayList<OnResultChanged> m_onResultChangedCallbackList;

    private DeregisterCallback m_deregisterCallback;
    private QueryResultsReadyCallback m_queryResultsReadyCallback;

    private int m_collapsedResults = 3;

    private ExpansionState m_expansionState;

    public SearchResultSet() {
        m_results = new ArrayList<SearchResult>();
        m_onResultChangedCallbackList = new ArrayList<OnResultChanged>();
        m_expansionState = ExpansionState.COLLAPSED;
    }

    public SearchResultSet(int collapsedResults) {
        m_results = new ArrayList<SearchResult>();
        m_onResultChangedCallbackList = new ArrayList<OnResultChanged>();
        m_collapsedResults = collapsedResults;
        m_expansionState = ExpansionState.COLLAPSED;
    }

    public QueryResultsReadyCallback getUpdateCallback() {
        m_queryResultsReadyCallback = new QueryResultsReadyCallback() {
            @Override
            public void onQueryCompleted(SearchResult[] returnedResults) {
                updateSetResults(returnedResults);
            }
        };

        return m_queryResultsReadyCallback;
    }

    public void createSearchProviderDeregistrationCallback(final SearchProvider provider){
        m_deregisterCallback = new DeregisterCallback(){
            @Override
            public void deregister() {
                provider.removeSearchCompletedCallback(m_queryResultsReadyCallback);
            }
        };
    }

    public void createSuggestionProviderDeregistrationCallback(final SuggestionProvider provider){
        m_deregisterCallback = new DeregisterCallback(){
            @Override
            public void deregister() {
                provider.removeSuggestionsReceivedCallback(m_queryResultsReadyCallback);
            }
        };
    }

    public void updateSetResults(SearchResult[] results) {
        m_results.clear();
        m_results.addAll(Arrays.asList(results));
        notifyObserversOnResultsChange();
    }

    public SearchResult getResult(int index) {
        return m_results.get(index);
    }

    public void setExpansionState(ExpansionState state){
        m_expansionState = state;
        notifyObserversOnResultsChange();
    }

    public int getVisibleResultCount() {
        switch(m_expansionState){
            case EXPANDED:
                return getResultCount();
            case COLLAPSED:
                return Math.min(m_results.size(), m_collapsedResults);
            case HIDDEN:
            default:
                return 0;
        }
    }

    public int getResultCount(){
        return m_results.size();
    }

    public SearchResult[] getResultsInRange(int min, int max) {
        if(min < 0) {
            min = 0;
        }

        if(min > m_results.size()) {
            return new SearchResult[0];
        }

        if (max > m_results.size()) {
            max = m_results.size();
        }

        if(max < min) {
            int t = min;
            max = min;
            min = t;
        }

        SearchResult[] results = new SearchResult[max - min];
        for(int i = min; i < max; ++i) {
            results[i-min] = m_results.get(i);
        }
        return results;
    }

    public void deregisterWithProvider(){
        m_deregisterCallback.deregister();
    }

    public void addOnResultChangedHandler(OnResultChanged callback) {
        m_onResultChangedCallbackList.add(callback);
    }

    public boolean isExpanded(){
        return m_expansionState == ExpansionState.EXPANDED;
    }

    public boolean hasFooter(){
        if(m_expansionState == ExpansionState.EXPANDED){
            return true;
        }
        if(m_expansionState == ExpansionState.COLLAPSED) {
            return getResultCount() - getVisibleResultCount() > 0;
        }
        return false;
    }

    private void notifyObserversOnResultsChange(){
        for(OnResultChanged callback:m_onResultChangedCallbackList){
            callback.invoke();
        }
    }
}
