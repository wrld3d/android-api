package com.wrld.widgets.searchbox;

public class DebugAutocompleteProvider implements AutocompleteProvider{

    private String m_uid;

    public DebugAutocompleteProvider(String uid)
    {
        m_uid = uid;
    }

    @Override
    public void getSuggestions(String query, OnAutocompleteSuggestionsReceivedCallback callback) {
        int numResults = 5;
        String[] results = new String[numResults];
        for (int i = 0; i < numResults; ++i) {
            results[i] = generateDebugResult(query, i);
        }

        callback.onAutocompleteSuggestionsReceived(results);
    }

    private String generateDebugResult(String query, int id) {
        return new String(m_uid + ": " + query + "(" + id + ")");
    }
}
