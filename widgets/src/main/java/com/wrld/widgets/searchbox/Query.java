package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;

class Query implements com.wrld.widgets.searchbox.api.Query {

    @Override
    public String getQueryString() { return m_queryString; }

    @Override
    public boolean hasCompleted(){ return m_isCompleted; }

    @Override
    public boolean wasCancelled(){ return m_isCompleted && !m_success; }

    private String m_queryString;
    private boolean m_isCompleted;
    private boolean m_wasCancelled;

    private ArrayList<SearchResult> m_allResultsFromProviders;

    private QueryResultsReadyCallback m_onQueryResultsReadyCallback;
    private boolean[] m_providersReturned;
    private boolean m_success;

    public Query(String queryString, QueryResultsReadyCallback onComplete, int numProviders){
        m_queryString = queryString;
        m_isCompleted = false;
        m_onQueryResultsReadyCallback = onComplete;
        m_allResultsFromProviders = new ArrayList<SearchResult>();
        m_providersReturned = new boolean[numProviders];
        for(int i = 0; i < numProviders; ++i){
            m_providersReturned[i] = false;
        }
        m_success = false;
    }

    public void addResults(int providerIndex, SearchResult[] returnedResults){
        if (!hasCompleted()) {
            m_allResultsFromProviders.addAll(Arrays.asList(returnedResults));
            m_providersReturned[providerIndex] = true;

            if (allProvidersReturned()) {
                m_success = true;
                completeQuery(m_success);
            }
        }
    }

    private boolean allProvidersReturned(){
        for(boolean hasReturned : m_providersReturned){
            if(!hasReturned){
                return false;
            }
        }
        return true;
    }

    private void completeQuery(boolean success){
        SearchResult[] resultsFromAllProviders = new SearchResult[m_allResultsFromProviders.size()];
        m_isCompleted = true;
        if(success){
            m_onQueryResultsReadyCallback.onQueryCompleted(m_allResultsFromProviders.toArray(resultsFromAllProviders));
        }
    }

    @Override
    public void cancel() {
        m_success = false;
        completeQuery(m_success);
    }
}
