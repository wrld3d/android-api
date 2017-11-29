// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import java.util.ArrayList;

public abstract class SearchProviderBase implements SearchProvider {
    private ArrayList<OnResultsReceivedCallback> m_onResultsReceivedCallbacks;

    private SearchResultViewFactory m_resultViewFactory;

    protected String m_title;

    public SearchProviderBase(String title){
        m_title = title;
        m_onResultsReceivedCallbacks = new ArrayList<OnResultsReceivedCallback>();
    }

    @Override
    public String getTitle() {return m_title;}

    @Override
    public void addOnResultsReceivedCallback(OnResultsReceivedCallback callback) {
        m_onResultsReceivedCallbacks.add(callback);
    }

    protected void performSearchCompletedCallbacks(SearchResult[] results){
        for(OnResultsReceivedCallback callback: m_onResultsReceivedCallbacks) {
            callback.onResultsReceived(results);
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
