// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class SearchResultSetTest {
    // create a concrete instance
    abstract SearchResultSet CreateSearchResultSet();

    protected SearchResultSet m_searchResultSet;

    public SearchResultSetTest(){
        m_searchResultSet = CreateSearchResultSet();
    }

    void populateWithResults(SearchResult[] results) {
        for(SearchResult result : results){
            m_searchResultSet.addResult(result);
        }
    }

    @Test
    public void testSortOnTitle() {

        int resultCount = 5;
        SearchResult[] mockResults = new SearchResult[resultCount];
        for(int i = 0; i < resultCount; ++i){
            mockResults[i] = mock(SearchResult.class);
            when(mockResults[i].getTitle()).thenReturn("Title " + (resultCount-(i+1)));
        }

        populateWithResults(mockResults);

        SearchResult[] results = m_searchResultSet.sortOn("Title");

        assertEquals(resultCount, results.length);

        for(int i = 0; i < resultCount; ++i){
            assertEquals("Title " + i, results[i].getTitle());
        }
    }

    @Test
    public void testSortOnDescriptionProperty() {

        int resultCount = 5;
        SearchResult[] mockResults = new SearchResult[resultCount];
        for(int i = 0; i < resultCount; ++i){
            mockResults[i] = mock(SearchResult.class);
            SearchResultProperty mockDescription = createMockProperty("Description", "Description " + (resultCount-(i+1)));
            when(mockResults[i].getProperty("Description")).thenReturn(mockDescription);
        }

        populateWithResults(mockResults);

        SearchResult[] results = m_searchResultSet.sortOn("Description");

        assertEquals(resultCount, results.length);

        for(int i = 0; i < resultCount; ++i){
            assertEquals("Description " + i, results[i].getProperty("Description").getValue());
        }
    }

    @Test
    public void testAddSearchResultIncreasesCountBy1() {
        SearchResult result = mock(SearchResult.class);
        int count = m_searchResultSet.getResultCount();
        m_searchResultSet.addResult(result);

        assertEquals(count+1, m_searchResultSet.getResultCount());
    }

    @Test
    public void testAddedSearchResultInsertedAtEndOfResults() {
        SearchResult result = mock(SearchResult.class);
        int count = m_searchResultSet.getResultCount();

        m_searchResultSet.addResult(result);

        assertEquals(result, m_searchResultSet.getAllResults()[count]);
    }

    @Test
    public void testClearEmptiesResultsSet() {
        populateWithResults(createMockResults(10));
        assertEquals(10, m_searchResultSet.getResultCount());
        m_searchResultSet.clear();
        assertEquals(0, m_searchResultSet.getResultCount());
    }

    @Test
    public void testRemoveSearchResultDecrementsCountBy1() {
        SearchResult[] results = createMockResults(10);
        populateWithResults(results);

        m_searchResultSet.removeResult(results[9]);

        assertEquals(9, m_searchResultSet.getResultCount());
    }

    @Test
    public void testRemoveSearchResultByIndexDecrementsCountBy1() {
        SearchResult[] results = createMockResults(10);
        populateWithResults(results);

        m_searchResultSet.removeResult(9);

        assertEquals(9, m_searchResultSet.getResultCount());
    }

    @Test
    public void testGetAllResultsReturnsCorrectAmount() {
        populateWithResults(createMockResults(10));
        assertEquals(10, m_searchResultSet.getAllResults().length);
    }

    @Test
    public void testGetResultReturnsCorrectResult() {
        SearchResult[] mockResults = createMockResults(5);
        populateWithResults(mockResults);
        assertEquals(mockResults[3], m_searchResultSet.getResult(3));
    }

    @Test
    public void testGetAllResultsReturnsInOrder() {
        SearchResult[] results = createMockResults(10);
        populateWithResults(results);
        assertArrayEquals(results, m_searchResultSet.getAllResults());
    }

    @Test
    public void testGetAllResultsCountReturnsCorrectAmount() {
        populateWithResults(createMockResults(10));
        assertEquals(10, m_searchResultSet.getResultCount());
    }

    @Test
    public void testGetResultsInRangeReturnsCorrectResultsInRange() {
        SearchResult[] results = createMockResults(10);
        populateWithResults(results);

        int startIndex = 3;
        int rangeSize = 4;

        SearchResult[] subList = new SearchResult[rangeSize];
        for(int i = 0; i < rangeSize; ++i){
            subList[i] = results[i + startIndex];
        }
        assertArrayEquals(subList, m_searchResultSet.getResultsInRange(startIndex, startIndex + rangeSize));
    }

    @Test
    public void testGetResultsInRangeDoesNotExceedResultSetSize() {

        int totalResults = 10;
        SearchResult[] results = createMockResults(totalResults);
        populateWithResults(results);

        int startIndex = 8;
        int expectedResults = totalResults - startIndex;

        SearchResult[] subList = new SearchResult[expectedResults];
        for(int i = 0; i < expectedResults; ++i){
            subList[i] = results[i + startIndex];
        }
        assertArrayEquals(subList, m_searchResultSet.getResultsInRange(startIndex, startIndex + totalResults));
    }

    protected SearchResult[] createMockResults(int count){

        SearchResult mockResults[] = new SearchResult[count];

        for(int i  = 0; i < count; ++i){
            mockResults[i] = mock(SearchResult.class);
            when(mockResults[i].getTitle()).thenReturn("Title " + i);
            SearchResultProperty<String> description = mock(SearchResultProperty.class);
            when(description.getKey()).thenReturn("Description");
            when(description.getValue()).thenReturn("Description " + i);
            when(mockResults[i].getProperty("Description")).thenReturn(description);
        }

        return mockResults;
    }

    protected SearchResultProperty<String> createMockProperty(String key, final String value)
    {
        SearchResultProperty<String> mockProperty = mock(SearchResultProperty.class);

        when(mockProperty.getKey()).thenReturn(key);
        when(mockProperty.getValue()).thenReturn(value);

        Answer<Integer> answer = new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                SearchResultProperty<String> other = invocation.getArgumentAt(0, SearchResultProperty.class);
                return value.compareTo(other.getValue());
            }
        };

        when(mockProperty.compareTo(any(SearchResultProperty.class))).thenAnswer(answer);

        return mockProperty;
    }
}
