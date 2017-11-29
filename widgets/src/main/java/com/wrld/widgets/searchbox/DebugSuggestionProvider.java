package com.wrld.widgets.searchbox;

import java.util.ArrayList;

public class DebugSuggestionProvider extends SuggestionProviderBase implements SuggestionProvider{

    private SearchResultViewFactory m_suggestionViewFactory;
    private ArrayList<OnResultsReceivedCallback> m_onSuggestionsReceivedCallback;

    private final String LOREM_IPSUM =
            "ex vis nusquam tincidunt, Lorem ipsum dolor sit amet.";

    public DebugSuggestionProvider(String uid) {
        super(uid);
    }

    @Override
    public void getSuggestions(String query) {

        int numResults = 5;

        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_title + " " + query);
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugSuggestion(i, query);
        }

        performSuggestionCompletedCallbacks(results);
    }

    @Override
    public void getSearchResults(String query) {

        int numResults = 100;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_title + ": " + query, new SearchResultStringProperty("Description", LOREM_IPSUM));
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugResult(i, query);
        }

        performSearchCompletedCallbacks(results);
    }

    private SearchResult generateDebugResult(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " result (" + id + ")", new SearchResultStringProperty("Description", LOREM_IPSUM));
    }

    private SearchResult generateDebugSuggestion(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " suggestion (" + id + ")");
    }
}
