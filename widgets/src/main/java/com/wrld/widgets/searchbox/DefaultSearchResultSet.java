// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved
package com.wrld.widgets.searchbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class DefaultSearchResultSet implements SearchResultSet {

    private ArrayList<SearchResult> m_results;

    private class UpdateResults implements OnSearchResultsReceivedCallback {
        public void onResultsReceived(SearchResult[] results) {
            m_results.clear();
            m_results.addAll(Arrays.asList(results));
        }
    }

    private UpdateResults m_updateResulteCallback = new UpdateResults();

    public OnSearchResultsReceivedCallback updateResultsViewCallback() {return m_updateResulteCallback; }

    public DefaultSearchResultSet() {
        m_results = new ArrayList<SearchResult>();
    }

    @Override
    public void addResult(SearchResult result) {
        m_results.add(result);
    }

    public void updateResultsView(SearchResult[] results) {
        m_results.clear();
        m_results.addAll(Arrays.asList(results));
    }

    @Override
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

    @Override
    public SearchResult[] getAllResults() {
        SearchResult[] results = new SearchResult[m_results.size()];
        return m_results.toArray(results);
    }

    @Override
    public SearchResult getResult(int index) {
        return m_results.get(index);
    }

    @Override
    public int getResultCount() {
        return m_results.size();
    }

    @Override
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

    @Override
    public void removeResult(SearchResult result) {
        m_results.remove(result);
    }

    @Override
    public void removeResult(int resultIndex) {
        m_results.remove(resultIndex);
    }

    @Override
    public void clear() {
        m_results.clear();
    }
}
