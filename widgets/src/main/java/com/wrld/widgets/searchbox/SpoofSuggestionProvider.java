package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.DefaultSearchResult;
import com.wrld.widgets.searchbox.api.DefaultSearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultPropertyString;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.SuggestionProviderBase;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import java.util.ArrayList;

public class SpoofSuggestionProvider extends SuggestionProviderBase implements SuggestionProvider {

    private int m_suggestionsReturned = 3;
    private int m_searchResultsReturned = 100;

    public SpoofSuggestionProvider(String uid, SearchResultViewFactory viewFactory) {
        super(uid);
        setSuggestionViewFactory(viewFactory);
    }


    public SpoofSuggestionProvider(String uid, SearchResultViewFactory viewFactory, int suggestionsReturned, int searchResultsReturned) {
        super(uid);
        setSuggestionViewFactory(viewFactory);
        m_suggestionsReturned = suggestionsReturned;
        m_searchResultsReturned = searchResultsReturned;
    }

    @Override
    public String getSuggestionTitleFormatting() {
        return "Spoof places '%d'";
    }

    @Override
    public void getSuggestions(com.wrld.widgets.searchbox.api.Query query) {

        SearchResult[] results = new SearchResult[m_suggestionsReturned];
        for(int i = 0; i < m_suggestionsReturned; ++i){
            results[i] = generateDebugSuggestion(i+1, query.getQueryString());
        }

        performSuggestionCompletedCallbacks(results);
    }

    @Override
    public void getSearchResults(com.wrld.widgets.searchbox.api.Query query) {
        SearchResult[] results = new SearchResult[m_searchResultsReturned];
        for(int i = 0; i < m_searchResultsReturned; ++i){
            results[i] = generateDebugResult(i+1, query.getQueryString());
        }

        performSearchCompletedCallbacks(results);
    }

    private SearchResult generateDebugResult(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " result (" + id + ")");
    }

    private SearchResult generateDebugSuggestion(int id, String query)
    {
        return new DefaultSearchResult(m_title + ": " + query + " suggestion (" + id + ")");
    }
}
