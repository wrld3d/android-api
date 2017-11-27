// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.graphics.drawable.Icon;

public interface SearchResult {
    String getTitle();

    SearchResultProperty getProperty(String propertyKey);
}
