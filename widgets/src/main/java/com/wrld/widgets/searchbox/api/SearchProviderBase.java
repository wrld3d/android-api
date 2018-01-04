package com.wrld.widgets.searchbox.api;

import com.wrld.widgets.searchbox.api.events.QueryCompletedCallback;

import java.util.ArrayList;

public abstract class SearchProviderBase implements SearchProvider {
    private ArrayList<QueryCompletedCallback> m_searchCompletedCallbacks;

    private SearchResultViewFactory m_resultViewFactory;

    protected String m_title;

    public SearchProviderBase(String title){
        m_title = title;
        m_searchCompletedCallbacks = new ArrayList<QueryCompletedCallback>();
    }

    @Override
    public String getTitle() {return m_title;}

    @Override
    public void addSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_searchCompletedCallbacks.add(queryCompletedCallback);
    }

    @Override
    public void removeSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_searchCompletedCallbacks.remove(queryCompletedCallback);
    }

    protected void performSearchCompletedCallbacks(SearchResult[] results){
        for(QueryCompletedCallback queryCompletedCallback : m_searchCompletedCallbacks) {
            queryCompletedCallback.onQueryCompleted(results);
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
