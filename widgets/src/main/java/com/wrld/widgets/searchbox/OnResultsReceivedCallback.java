package com.wrld.widgets.searchbox;

import android.support.annotation.UiThread;

public interface OnResultsReceivedCallback {
    /**
     * Called when search results are returned from a SearchProvider
     ***/
    @UiThread
    void onResultsReceived(SearchResult[] results);
}
