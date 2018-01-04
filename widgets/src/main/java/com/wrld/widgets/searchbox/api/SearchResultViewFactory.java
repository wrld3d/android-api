package com.wrld.widgets.searchbox.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface SearchResultViewFactory {
    View makeSearchResultView(LayoutInflater inflater, SearchResult model, ViewGroup parent);
    SearchResultViewHolder makeSearchResultViewHolder();
}
