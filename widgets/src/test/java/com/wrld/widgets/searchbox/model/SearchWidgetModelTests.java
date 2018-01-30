package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultProperty;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchWidgetModelTests {

    public MockSearchProvider createValidSearchProvider()
    {
        return new MockSearchProvider("Valid Search Provider", true);
    }

    public SearchWidgetModel createSearchWidgetModel()
    {
        return new SearchWidgetModel();
    }


    @Test
    public void testQueryTextWasSearchedFor() throws Exception {
        SearchWidgetModel widgetModel = createSearchWidgetModel();
        MockSearchProvider provider = createValidSearchProvider();
        widgetModel.addSearchProvider(provider);

        widgetModel.doSearch("Hello");

        assertEquals(provider.searchedQuery(), "Hello");
        assertEquals(provider.searchedContext(), null);
    }

    @Test
    public void testQueryContextWasSearchedFor() throws Exception {
        SearchWidgetModel widgetModel = createSearchWidgetModel();
        MockSearchProvider provider = createValidSearchProvider();
        widgetModel.addSearchProvider(provider);

        widgetModel.doSearch("Hello", "Context");

        assertEquals(provider.searchedQuery(), "Hello");
        assertEquals(provider.searchedContext(), "Context");
    }

}

class MockSearchProvider implements ISearchProvider
{
    Boolean m_willSucceed;
    String m_title;
    QueryResultsReadyCallback m_callback;
    private boolean m_cancelled;

    public String m_searchedQuery;
    public Object m_searchedContext;

    public class MockSearchResult implements SearchResult
    {
        String m_title;

        public MockSearchResult(String title)
        {
            m_title = title;
        }

        @Override
        public String getTitle() {
            return m_title;
        }

        @Override
        public boolean hasProperty(String propertyKey) {
            return false;
        }

        @Override
        public SearchResultProperty getProperty(String propertyKey) {
            return null;
        }
    }

    public MockSearchProvider(String title, Boolean willSuceed)
    {
        m_title = title;
        m_willSucceed = willSuceed;
    }


    @Override
    public String getTitle() {
        return m_title;
    }

    public String searchedQuery() { return m_searchedQuery; }
    public Object searchedContext() { return m_searchedContext; }

    @Override
    public void getSearchResults(String queryText, Object queryContext) {

        m_searchedQuery = queryText;
        m_searchedContext = queryContext;

        if(!m_willSucceed)
        {
            m_callback.onQueryCompleted(new MockSearchResult[0], false);
        }
        else
        {
            MockSearchResult[] results = new MockSearchResult[1];
            results[0] = new MockSearchResult("Search Result");
            m_callback.onQueryCompleted(results, true);
        }
    }

    @Override
    public void cancelSearch() {
        m_cancelled = true;
        m_callback.onQueryCancelled();
    }

    @Override
    public void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_callback = queryResultsReadyCallback;
    }

    @Override
    public void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_callback = null;
    }

    @Override
    public SearchResultViewFactory getResultViewFactory() {
        return null;
    }
}
