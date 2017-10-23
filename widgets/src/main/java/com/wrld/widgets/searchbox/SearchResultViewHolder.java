// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.View;

public interface SearchResultViewHolder {
    void initialise(View view);
    void populate(SearchResult searchResult);
}
