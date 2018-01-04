package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;

public abstract class SearchProviderBase implements SearchProvider {
    private ArrayList<QueryResultsReadyCallback> m_searchCompletedCallbacks;

    private SearchResultViewFactory m_resultViewFactory;

    protected String m_title;

    public SearchProviderBase(String title){
        m_title = title;
        m_searchCompletedCallbacks = new ArrayList<QueryResultsReadyCallback>();
    }

    @Override
    public String getTitle() {return m_title;}

    @Override
    public void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_searchCompletedCallbacks.add(queryResultsReadyCallback);
    }

    @Override
    public void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_searchCompletedCallbacks.remove(queryResultsReadyCallback);
    }

    protected void performSearchCompletedCallbacks(SearchResult[] results){
        QueryResultsReadyCallback[] queryResultsReadyCallbacks = new QueryResultsReadyCallback[m_searchCompletedCallbacks.size()];
        m_searchCompletedCallbacks.toArray(queryResultsReadyCallbacks);
        for(QueryResultsReadyCallback queryResultsReadyCallback : queryResultsReadyCallbacks) {
            queryResultsReadyCallback.onQueryCompleted(results);
        }
    }

    @Override
    public void setResultViewFactory(SearchResultViewFactory factory){
        m_resultViewFactory = factory;
    }

    @Override
    public SearchResultViewFactory getResultViewFactory() {
        return m_resultViewFactory;
    }
}
