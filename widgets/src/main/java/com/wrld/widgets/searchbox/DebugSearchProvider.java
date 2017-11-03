// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import java.util.ArrayList;

/**
 * Returns a random number of results for any query
 */
public class DebugSearchProvider implements SearchProvider {
    private final String LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, ex vis nusquam tincidunt.";

    protected String m_uid;
    private SearchResultViewFactory m_resultViewFactory;
    private ArrayList<OnResultsReceivedCallback> m_onResultsReceivedCallback;

    public DebugSearchProvider(String uid)
    {
        m_uid = uid;
        m_onResultsReceivedCallback = new ArrayList<OnResultsReceivedCallback>();
    }

    @Override
    public void getSearchResults(String query) {
        int numResults = 100;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_uid + ": " + query, new SearchResultStringProperty("Description", LOREM_IPSUM));
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugResult(i, query);
        }

        for(OnResultsReceivedCallback callback: m_onResultsReceivedCallback) {
            callback.onResultsReceived(results);
        }
    }

    @Override
    public void addOnResultsRecievedCallback(OnResultsReceivedCallback callback) {
        m_onResultsReceivedCallback.add(callback);
    }

    @Override
    public void setResultViewFactory(SearchResultViewFactory factory){
        m_resultViewFactory = factory;
    }

    @Override
    public SearchResultViewFactory getResultViewFactory() {
        return m_resultViewFactory;
    }

    private SearchResult generateDebugResult(int id, String query)
    {
        return new DefaultSearchResult(m_uid + ": " + query + " (" + id + ")", new SearchResultStringProperty("Description", LOREM_IPSUM));
    }

}
