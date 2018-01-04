// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.DefaultSearchResult;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultProperty;

public class DefaultSearchResultTest extends SearchResultTest {
    @Override
    SearchResult CreateSearchResult(String title, SearchResultProperty... additionalProperties) {
        return new DefaultSearchResult(title, additionalProperties);
    }
}
