package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

    @Test
    public void testCurrentQueryWasSet() throws Exception {
        SearchWidgetModel widgetModel = createSearchWidgetModel();
        MockSearchProvider provider = createValidSearchProvider();
        widgetModel.addSearchProvider(provider);

        widgetModel.doSearch("Hello", "Context");

        assertNotEquals(widgetModel.getCurrentQuery(), null);
        assertEquals(widgetModel.getCurrentQuery().getQueryString(), "Hello");
        assertEquals(widgetModel.getCurrentQuery().getQueryContext(), "Context");
    }

    @Test
    public void testCurrentQueryResultsWereReturned() throws Exception {
        SearchWidgetModel widgetModel = createSearchWidgetModel();
        MockSearchProvider provider = createValidSearchProvider();
        widgetModel.addSearchProvider(provider);

        widgetModel.doSearch("Hello", "Context");

        assertNotEquals(widgetModel.getCurrentQueryResults(), null);
        assertEquals(widgetModel.getCurrentQueryResults().size(), 1);
        assertEquals(widgetModel.getCurrentQueryResults().get(0).wasSuccess(), true);
        assertEquals(widgetModel.getCurrentQueryResults().get(0).getResults().length, 1);
        assertEquals(widgetModel.getCurrentQueryResults().get(0).getResults()[0].getTitle(), "Search Result");
    }

    // Test clearing results

    // Test cancelling query in progress

    // Test cancelling query before started

    // Test doing search twice cancels previous

    // And everything else./

}

class MockSearchProvider implements ISearchProvider
{
    Boolean m_willSucceed;
    String m_title;
    ISearchProviderResultsReadyCallback m_callback;
    private boolean m_cancelled;

    public String m_searchedQuery;
    public Object m_searchedContext;

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
            m_callback.onQueryCompleted(new DefaultSearchResult[0], false);
        }
        else
        {
            DefaultSearchResult[] results = new DefaultSearchResult[1];
            results[0] = new DefaultSearchResult("Search Result");
            m_callback.onQueryCompleted(results, true);
        }
    }

    @Override
    public void cancelSearch() {
        m_cancelled = true;
        //m_callback.onQueryCancelled();
    }

    @Override
    public void addSearchCompletedCallback(ISearchProviderResultsReadyCallback queryResultsReadyCallback) {
        m_callback = queryResultsReadyCallback;
    }

    @Override
    public void removeSearchCompletedCallback(ISearchProviderResultsReadyCallback queryResultsReadyCallback) {
        m_callback = null;
    }

    @Override
    public ISearchResultViewFactory getResultViewFactory() {
        return null;
    }
}
