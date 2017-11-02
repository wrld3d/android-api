package com.wrld.widgets.searchbox;

import java.util.ArrayList;

public class DebugSuggestionProvider extends DebugSearchProvider implements SuggestionProvider{

    private SearchResultViewFactory m_suggestionViewFactory;
    private ArrayList<OnResultsReceivedCallback> m_onSuggestionsReceivedCallback;

    private final String LOREM_IPSUM =
            "ex vis nusquam tincidunt, Lorem ipsum dolor sit amet.";
    public DebugSuggestionProvider(String uid) {
        super(uid);
        m_onSuggestionsReceivedCallback = new ArrayList<OnResultsReceivedCallback>();
    }

    @Override
    public void getSuggestions(String query) {

        int numResults = 5;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_uid + ": Suggestion : " + query);
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugSuggestion(i);
        }

        for(OnResultsReceivedCallback callback: m_onSuggestionsReceivedCallback) {
            callback.onResultsReceived(results);
        }
    }

    @Override
    public void addOnSuggestionsRecievedCallback(OnResultsReceivedCallback callback) {
        m_onSuggestionsReceivedCallback.add(callback);
    }

    @Override
    public void setSuggestionViewFactory(SearchResultViewFactory factory){
        m_suggestionViewFactory = factory;
    }




    private SearchResult generateDebugSuggestion(int id)
    {
        return new DefaultSearchResult(m_uid + ": Suggestion : " + id);
    }
}
