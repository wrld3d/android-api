package com.wrld.widgets.searchbox.model;

import com.wrld.widgets.searchbox.view.ISearchResultViewFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SearchWidgetModelTests {

    private SearchResultsModel m_results;
    private SearchModel m_widgetModel;
    private MockSearchProvider m_provider;

    @Before
    public void setUp() {
        m_results = new SearchResultsModel();
        m_widgetModel = new SearchModel(m_results);
        m_provider = new MockSearchProvider("Valid Search Provider", true);
        m_widgetModel.addSearchProvider(m_provider);
    }

    @After
    public void teardown() {

    }


    @Test
    public void testQueryTextWasSearchedFor() throws Exception {
        m_widgetModel.doSearch("Hello");

        assertEquals(m_provider.searchedQuery(), "Hello");
        assertEquals(m_provider.searchedContext(), null);
    }

    @Test
    public void testQueryContextWasSearchedFor() throws Exception {
        m_widgetModel.doSearch("Hello", "Context");

        assertEquals(m_provider.searchedQuery(), "Hello");
        assertEquals(m_provider.searchedContext(), "Context");
    }

    @Test
    public void testCurrentQueryWasSet() throws Exception {
        m_widgetModel.doSearch("Hello", "Context");

        assertNotEquals(m_widgetModel.getCurrentQuery(), null);
        assertEquals(m_widgetModel.getCurrentQuery().getQueryString(), "Hello");
        assertEquals(m_widgetModel.getCurrentQuery().getQueryContext(), "Context");
    }

    @Test
    public void testCurrentQueryResultsWereReturned() throws Exception {
        m_widgetModel.doSearch("Hello", "Context");

        assertNotEquals(m_results.getCurrentQueryResults(), null);
        assertEquals(m_results.getCurrentQueryResults().size(), 1);
        assertEquals(m_results.getCurrentQueryResults().get(0).wasSuccess(), true);
        assertEquals(m_results.getCurrentQueryResults().get(0).getResults().length, 1);
        assertEquals(m_results.getCurrentQueryResults().get(0).getResults()[0].getTitle(), "Search Result");
    }

    @Test
    public void testCurrentQueryResultsAreEmptyAfterClear() throws Exception {

        m_widgetModel.doSearch("Hello", "Context");
        m_widgetModel.clear();

        assertEquals(m_results.getCurrentQueryResults(), null);
    }
    // Test cancelling query in progress

    // Test cancelling query before started

    // Test doing search twice cancels previous

    // And everything else./

}

class MockSearchProvider implements SearchProvider
{
    Boolean m_willSucceed;
    String m_title;
    SearchProviderResultsReadyCallback m_callback;
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
    public void addSearchCompletedCallback(SearchProviderResultsReadyCallback queryResultsReadyCallback) {
        m_callback = queryResultsReadyCallback;
    }

    @Override
    public void removeSearchCompletedCallback(SearchProviderResultsReadyCallback queryResultsReadyCallback) {
        m_callback = null;
    }

    @Override
    public ISearchResultViewFactory getResultViewFactory() {
        return null;
    }
}
