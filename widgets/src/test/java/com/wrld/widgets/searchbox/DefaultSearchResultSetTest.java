// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;
import static org.mockito.Mockito.mock;

public class DefaultSearchResultSetTest extends SearchResultSetTest {

    DefaultSearchResultSet m_Default_searchResultSet;

    public SearchResultSet CreateSearchResultSet(){
        m_Default_searchResultSet = new DefaultSearchResultSet();
        return m_Default_searchResultSet;
    }

    @Override
    void PopulateWithResults(SearchResult[] results) {
        for(SearchResult result : results){
            m_Default_searchResultSet.addResult(result);
        }
    }
}
