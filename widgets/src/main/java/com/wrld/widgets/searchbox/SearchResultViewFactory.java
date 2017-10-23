// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface SearchResultViewFactory {
    View makeSearchResultView(LayoutInflater inflater, SearchResult model, ViewGroup parent);
    SearchResultViewHolder makeSearchResultViewHolder();
}
