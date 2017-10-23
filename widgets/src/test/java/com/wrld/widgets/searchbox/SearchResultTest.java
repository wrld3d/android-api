// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class SearchResultTest {

    // create a concrete instance
    abstract SearchResult CreateSearchResult(String title, String description, SearchResultProperty... additionalProperties);

    final String DefaultTitle = "Search Result Title";
    final String LoremIpsum =
            "Lorem ipsum dolor sit amet, ex vis nusquam tincidunt. Ut vim case incorrupte, " +
            "discere nominavi qui et, usu te odio salutatus. Exerci altera possit mei te. Mel agam etiam mandamus in. " +
            "Pro ex vocent sadipscing, te autem essent est. Usu suas ullum cu, justo oratio dolorum vis et.";

    protected SearchResult m_searchResult;

    public SearchResultTest(){
        m_searchResult = CreateSearchResult(DefaultTitle, LoremIpsum);
    }

    @Test
    public void checkTitleAssignmentWhenCreated()
    {
        assertEquals(DefaultTitle, m_searchResult.getTitle());
    }

    @Test
    public void checkDescriptionAssignmentKeyWhenCreated()
    {
        assertEquals("Description", m_searchResult.getDescription().getKey());
    }

    @Test
    public void checkDescriptionAssignmentValueWhenCreated()
    {
        assertEquals(LoremIpsum, m_searchResult.getDescription().getValue());
    }

    @Test
    public void checkAdditionalPropertyAssignment()
    {
        String propKey = "Property 1 Key";
        String propValue = "Property 1 Value";
        SearchResultProperty<String> mockProperty = mock(SearchResultProperty.class);
        when(mockProperty.getKey()).thenReturn(propKey);
        when(mockProperty.getValue()).thenReturn(propValue);

        SearchResult searchResult = CreateSearchResult(DefaultTitle, LoremIpsum, mockProperty);
        assertEquals(propValue, searchResult.getProperty(propKey).getValue());
    }

}
