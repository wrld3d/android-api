// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

/**
 * Returns a random number of results for any query
 */
public class DebugSearchProvider implements SearchProvider {
    private final String LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, ex vis nusquam tincidunt.";

    private String m_uid;

    public DebugSearchProvider(String uid)
    {
        m_uid = uid;
    }

    @Override
    public void getSearchResults(String query, OnSearchResultsReceivedCallback callback) {
        int numResults = 5;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_uid + ": " + query, LOREM_IPSUM);
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugResult(i);
        }

        callback.onResultsReceived(results);
    }

    private SearchResult generateDebugResult(int id)
    {
        return new DefaultSearchResult(m_uid + ": " + id, id + ": " + LOREM_IPSUM);
    }

}
