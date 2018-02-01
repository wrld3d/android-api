package com.wrld.searchproviders;

import com.wrld.widgets.searchbox.model.ISearchProvider;
import com.wrld.widgets.searchbox.model.ISearchProviderResultsReadyCallback;
import com.wrld.widgets.searchbox.model.ISearchResult;
import com.wrld.widgets.searchbox.model.ISuggestionProvider;
import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import java.util.ArrayList;

public abstract class SearchProviderBase implements ISearchProvider, ISuggestionProvider {
    private ArrayList<ISearchProviderResultsReadyCallback> m_searchCompletedCallbacks;
    private ArrayList<ISearchProviderResultsReadyCallback> m_suggestionCompletedCallbacks;

    private ISearchResultViewFactory m_resultViewFactory;
    private ISearchResultViewFactory m_suggestionResultViewFactory;

    protected String m_title;

    public SearchProviderBase(String title){
        m_title = title;
        m_searchCompletedCallbacks = new ArrayList<>();
        m_suggestionCompletedCallbacks = new ArrayList<>();
    }

    @Override
    public String getTitle() {return m_title;}

    @Override
    public String getSuggestionTitleFormatting() { return m_title; }

    protected void performSearchCompletedCallbacks(ISearchResult[] results, Boolean success){
        // TODO: Threading concern?
        for(ISearchProviderResultsReadyCallback queryResultsReadyCallback : m_searchCompletedCallbacks) {
            queryResultsReadyCallback.onQueryCompleted(results, success);
        }
    }

    protected void performSuggestionCompletedCallbacks(ISearchResult[] results, Boolean success){
        // TODO: Threading concern?
        for(ISearchProviderResultsReadyCallback queryResultsReadyCallback : m_suggestionCompletedCallbacks) {
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
    public void addSearchCompletedCallback(ISearchProviderResultsReadyCallback resultReadyCallback) {
        m_searchCompletedCallbacks.add(resultReadyCallback);
    }

    @Override
    public void removeSearchCompletedCallback(ISearchProviderResultsReadyCallback resultReadyCallback) {
        m_searchCompletedCallbacks.remove(resultReadyCallback);
    }
    @Override
    public void addSuggestionsReceivedCallback(ISearchProviderResultsReadyCallback resultReadyCallback) {
        m_suggestionCompletedCallbacks.add(resultReadyCallback);
    }

    @Override
    public void removeSuggestionsReceivedCallback(ISearchProviderResultsReadyCallback resultReadyCallback) {
        m_suggestionCompletedCallbacks.remove(resultReadyCallback);
    }


}
