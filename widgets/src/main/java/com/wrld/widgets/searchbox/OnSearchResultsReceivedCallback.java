// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when the EegeoMap object has been created and
 * is ready to call.
 */
public interface OnSearchResultsReceivedCallback {
    /**
     * Called when search results are returned from a SearchProvider
     ***/
    @UiThread
    void onResultsReceived(SearchResult[] results);
}
