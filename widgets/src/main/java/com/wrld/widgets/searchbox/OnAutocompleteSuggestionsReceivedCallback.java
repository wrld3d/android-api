package com.wrld.widgets.searchbox;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when the EegeoMap object has been created and
 * is ready to call.
 */
public interface OnAutocompleteSuggestionsReceivedCallback {
    /**
     * Called when search autocomplete suggestions are returned from an IAutoCompleteProvider
     ***/
    @UiThread
    void onAutocompleteSuggestionsReceived(String[] suggestions);
}
