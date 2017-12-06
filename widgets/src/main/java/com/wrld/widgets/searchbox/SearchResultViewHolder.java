package com.wrld.widgets.searchbox;

import android.view.View;

public interface SearchResultViewHolder {
    void initialise(View view);
    void populate(SearchResult searchResult);
}
