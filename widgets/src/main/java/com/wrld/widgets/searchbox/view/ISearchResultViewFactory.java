package com.wrld.widgets.searchbox.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrld.widgets.searchbox.model.ISearchResult;

public interface ISearchResultViewFactory {
    View makeSearchResultView(LayoutInflater inflater, ViewGroup parent);
    ISearchResultViewHolder makeSearchResultViewHolder();
}

