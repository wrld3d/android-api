// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import com.eegeo.mapapi.geometry.LatLng;

public class DefaultSearchResultTest extends SearchResultTest {
    @Override
    SearchResult CreateSearchResult(String title, String description, SearchResultProperty... additionalProperties) {
        return new DefaultSearchResult(title, description, additionalProperties);
    }
}
