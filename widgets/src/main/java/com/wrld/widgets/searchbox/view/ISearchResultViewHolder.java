package com.wrld.widgets.searchbox.view;

import android.view.View;

import com.wrld.widgets.searchbox.model.ISearchResult;

public interface ISearchResultViewHolder {
    void initialise(View view);
    void populate(ISearchResult searchResult);
}
