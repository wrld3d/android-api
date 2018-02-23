package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultsModel implements ObservableSearchResultsModel {

    private List<SearchProviderQueryResult> m_currentQueryResults;
    private List<SearchResultsListener> m_resultsListeners;

    public SearchResultsModel()
    {
        m_currentQueryResults = null;
        m_resultsListeners = new ArrayList<>();
    }

    public final List<SearchProviderQueryResult> getCurrentQueryResults() { return m_currentQueryResults; }

    public void setResultsForQuery(List<SearchProviderQueryResult> results, SearchQuery query) {
        m_currentQueryResults = results;
        if(m_currentQueryResults != null) {
            Collections.sort(m_currentQueryResults);
        }

        for(SearchResultsListener listener : m_resultsListeners) {
            listener.onSearchResultsRecieved(query, m_currentQueryResults);
        }
    }

    public int getTotalCurrentQueryResults() {
        if(m_currentQueryResults == null) {
            return 0;
        }
        int total = 0;
        for(SearchProviderQueryResult result : m_currentQueryResults) {
            if(result.getResults() != null && result.wasSuccess()) {
                total += result.getResults().length;
            }
        }
        return total;
    }

    public void selectSearchResult(SearchResult result) {
        result.select();

        for (SearchResultsListener listener : m_resultsListeners) {
            listener.onSearchResultsSelected(result);
        }
    }

    public void addResultListener(SearchResultsListener listener)  {
        m_resultsListeners.add(listener);
    }

    public void removeResultListener(SearchResultsListener listener)  {
        m_resultsListeners.remove(listener);
    }

    public void clear() {

        if(m_currentQueryResults != null) {
            m_currentQueryResults = null;

            for (SearchResultsListener listener : m_resultsListeners) {
                listener.onSearchResultsCleared();
            }
        }
    }
}

