package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.DefaultSearchResult;
import com.wrld.widgets.searchbox.api.SearchProviderBase;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultPropertyString;

/**
 * Returns a random number of results for any query
 */
class SpoofSearchProvider extends SearchProviderBase {
    private final String LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, ex vis nusquam tincidunt.";

    public SpoofSearchProvider(String title)
    {
        super(title);
    }

    @Override
    public void getSearchResults(com.wrld.widgets.searchbox.api.Query query) {

        int numResults = 100;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_title + ": " + query, new SearchResultPropertyString("Description", LOREM_IPSUM));
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugResult(i, query.getQueryString());
        }

        performSearchCompletedCallbacks(results);
    }

    private SearchResult generateDebugResult(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " result (" + id + ")", new SearchResultPropertyString("Description", LOREM_IPSUM));
    }

}
