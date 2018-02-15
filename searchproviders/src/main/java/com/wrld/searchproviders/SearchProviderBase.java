package com.wrld.searchproviders;

import com.wrld.widgets.searchbox.model.SearchProvider;
import com.wrld.widgets.searchbox.model.SearchProviderResultsReadyCallback;
import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.SuggestionProvider;
import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import java.util.ArrayList;

public abstract class SearchProviderBase implements SearchProvider, SuggestionProvider {
    private ArrayList<SearchProviderResultsReadyCallback> m_searchCompletedCallbacks;
    private ArrayList<SearchProviderResultsReadyCallback> m_suggestionCompletedCallbacks;

    private ISearchResultViewFactory m_resultViewFactory;
    private ISearchResultViewFactory m_suggestionResultViewFactory;

    protected String m_title;

    public SearchProviderBase(String title,
                              ISearchResultViewFactory resultViewfactory,
                              ISearchResultViewFactory suggestionViewfactory){
        m_title = title;
        m_searchCompletedCallbacks = new ArrayList<>();
        m_suggestionCompletedCallbacks = new ArrayList<>();
        m_resultViewFactory = resultViewfactory;
        m_suggestionResultViewFactory = suggestionViewfactory;
    }

    @Override
    public String getTitle() {return m_title;}

    @Override
    public String getSuggestionTitleFormatting() { return m_title; }

    protected void performSearchCompletedCallbacks(SearchResult[] results, Boolean success){
        // TODO: Threading concern?
        for(SearchProviderResultsReadyCallback queryResultsReadyCallback : m_searchCompletedCallbacks) {
            queryResultsReadyCallback.onQueryCompleted(results, success);
        }
    }

    protected void performSuggestionCompletedCallbacks(SearchResult[] results, Boolean success){
        // TODO: Threading concern?
        for(SearchProviderResultsReadyCallback queryResultsReadyCallback : m_suggestionCompletedCallbacks) {
            queryResultsReadyCallback.onQueryCompleted(results, success);
        }
    }

    @Override
    public void getSuggestions(String queryText, Object queryContext)
    {
        performSuggestionCompletedCallbacks(null, false);
    }

    public void setResultViewFactory(ISearchResultViewFactory factory){
        m_resultViewFactory = factory;
    }

    @Override
    public ISearchResultViewFactory getResultViewFactory() {
        return m_resultViewFactory;
    }

    public void setSuggestionViewFactory(ISearchResultViewFactory factory){
        m_suggestionResultViewFactory = factory;
    }

    @Override
    public ISearchResultViewFactory getSuggestionViewFactory() {
        return m_suggestionResultViewFactory;
    }

    @Override
    public void addSearchCompletedCallback(SearchProviderResultsReadyCallback resultReadyCallback) {
        m_searchCompletedCallbacks.add(resultReadyCallback);
    }

    @Override
    public void removeSearchCompletedCallback(SearchProviderResultsReadyCallback resultReadyCallback) {
        m_searchCompletedCallbacks.remove(resultReadyCallback);
    }
    @Override
    public void addSuggestionsReceivedCallback(SearchProviderResultsReadyCallback resultReadyCallback) {
        m_suggestionCompletedCallbacks.add(resultReadyCallback);
    }

    @Override
    public void removeSuggestionsReceivedCallback(SearchProviderResultsReadyCallback resultReadyCallback) {
        m_suggestionCompletedCallbacks.remove(resultReadyCallback);
    }


}
