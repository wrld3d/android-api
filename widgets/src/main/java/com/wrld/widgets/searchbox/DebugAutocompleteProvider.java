package com.wrld.widgets.searchbox;

public class DebugAutocompleteProvider implements AutocompleteProvider{

    private String m_uid;
    private final String LOREM_IPSUM =
            "ex vis nusquam tincidunt, Lorem ipsum dolor sit amet.";
    public DebugAutocompleteProvider(String uid)
    {
        m_uid = uid;
    }

    @Override
    public void getSuggestions(String query, OnResultsReceivedCallback callback) {
        int numResults = 5;
        SearchResult[] results = new SearchResult[numResults];
        results[0] = new DefaultSearchResult(m_uid + ": " + query);
        for(int i = 1; i < numResults; ++i){
            results[i] = generateDebugResult(i);
        }

        callback.onResultsReceived(results);
    }

    private SearchResult generateDebugResult(int id)
    {
        return new DefaultSearchResult(m_uid + ": " + id);
    }
}
