package com.wrld.widgets.searchbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class SearchResultSet {

    interface OnResultChanged{
        void invoke();
    }

    private interface DeregisterCallback {
        void deregister();
    }

    private ArrayList<SearchResult> m_results;
    private ArrayList<OnResultChanged> m_onResultChangedCallbackList;

    private DeregisterCallback m_deregisterCallback;
    private OnResultsReceivedCallback m_resultsReceivedCallback;

    public SearchResultSet() {
        m_results = new ArrayList<SearchResult>();
        m_onResultChangedCallbackList = new ArrayList<OnResultChanged>();
    }

    public OnResultsReceivedCallback getUpdateCallback() {
        m_resultsReceivedCallback = new OnResultsReceivedCallback() {
            @Override
            public void onResultsReceived(SearchResult[] results) {
                updateSetResults(results);
            }
        };

        return m_resultsReceivedCallback;
    }

    public void createSearchProviderDeregistrationCallback(final SearchProvider provider){
        m_deregisterCallback = new DeregisterCallback(){
            @Override
            public void deregister() {
                provider.removeOnResultsReceivedCallback(m_resultsReceivedCallback);
            }
        };
    }

    public void createSuggestionProviderDeregistrationCallback(final SuggestionProvider provider){
        m_deregisterCallback = new DeregisterCallback(){
            @Override
            public void deregister() {
                provider.removeOnSuggestionsReceivedCallback(m_resultsReceivedCallback);
            }
        };
    }

    public void updateSetResults(SearchResult[] results) {
        m_results.clear();
        m_results.addAll(Arrays.asList(results));

        for(OnResultChanged callback:m_onResultChangedCallbackList){
            callback.invoke();
        }
    }

    public void addResult(SearchResult result) {
        m_results.add(result);
    }

    public SearchResult[] sortOn(String propertyKey) {
        if(propertyKey == "Title"){
            return sortOnTitle();
        }

        return sortOnProperty(propertyKey);
    }

    private SearchResult[] sortOnTitle() {

        SearchResult[] allResults = getAllResults();

        Arrays.sort(allResults, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        return allResults;
    }

    private SearchResult[] sortOnProperty(final String property) {

        SearchResult[] allResults = getAllResults();

        Arrays.sort(allResults, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                if(o1.getProperty(property) == null && o2.getProperty(property) == null){
                    return 0;
                }

                if(o1.getProperty(property) == null){
                    return 1;
                }

                if(o2.getProperty(property) == null){
                    return -1;
                }

                return o1.getProperty(property).compareTo(o2.getProperty(property));
            }
        });

        return allResults;
    }

    public SearchResult[] getAllResults() {
        SearchResult[] results = new SearchResult[m_results.size()];
        return m_results.toArray(results);
    }

    public SearchResult getResult(int index) {
        return m_results.get(index);
    }

    public int getResultCount() {
        return m_results.size();
    }

    public SearchResult[] getResultsInRange(int min, int max) {
        if(min < 0) {
            min = 0;
        }

        if(min > m_results.size()) {
            return new SearchResult[0];
        }

        if (max > m_results.size()) {
            max = m_results.size();
        }

        if(max < min) {
            int t = min;
            max = min;
            min = t;
        }

        SearchResult[] results = new SearchResult[max - min];
        for(int i = min; i < max; ++i) {
            results[i-min] = m_results.get(i);
        }
        return results;
    }

    public void removeResult(SearchResult result) {
        m_results.remove(result);
    }

    public void removeResult(int resultIndex) {
        m_results.remove(resultIndex);
    }

    public void clear() {
        m_results.clear();
    }

    public void deregisterWithProvider(){
        m_deregisterCallback.deregister();
    }

    public void addOnResultChangedHandler(OnResultChanged callback) {
        m_onResultChangedCallbackList.add(callback);
    }
}
