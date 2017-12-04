// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

/**
 * Returns a random number of results for any query
 */
public class DebugSearchProvider extends SearchProviderBase {
    private final String LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, ex vis nusquam tincidunt.";

    public DebugSearchProvider(String title)
    {
        super(title);
    }

    @Override
    public void getSearchResults(String query) {

        int numResults = 100;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_title + ": " + query, new SearchResultPropertyString("Description", LOREM_IPSUM));
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugResult(i, query);
        }

        performSearchCompletedCallbacks(results);
    }

    private SearchResult generateDebugResult(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " result (" + id + ")", new SearchResultPropertyString("Description", LOREM_IPSUM));
    }

}
