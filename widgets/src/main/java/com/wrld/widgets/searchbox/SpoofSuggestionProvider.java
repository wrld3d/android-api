package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.DefaultSearchResult;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultPropertyString;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.SuggestionProviderBase;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;

class SpoofSuggestionProvider extends SuggestionProviderBase implements SuggestionProvider {

    private SearchResultViewFactory m_suggestionViewFactory;
    private ArrayList<QueryResultsReadyCallback> m_onSuggestionsReceivedCallback;

    private final String LOREM_IPSUM =
            "ex vis nusquam tincidunt, Lorem ipsum dolor sit amet.";

    public SpoofSuggestionProvider(String uid) {
        super(uid);
    }

    @Override
    public String getSuggestionTitleFormatting() {
        return "Spoof places '%d'";
    }

    @Override
    public void getSuggestions(com.wrld.widgets.searchbox.api.Query query) {

        int numResults = 5;

        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_title + " " + query);
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugSuggestion(i, query.getQueryString());
        }

        performSuggestionCompletedCallbacks(results);
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

    private SearchResult generateDebugSuggestion(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " suggestion (" + id + ")");
    }
}
