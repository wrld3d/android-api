package com.wrld.widgets.searchbox.view;

import android.view.View;

import com.wrld.widgets.searchbox.model.SearchResult;

public interface ISearchResultViewHolder {
    void initialise(View view);
    void populate(SearchResult searchResult,
                  String searchTerm,
                  boolean firstResultInSet,
                  boolean lastResultInSet);
}
