package com.wrld.widgets.searchbox;

import android.support.annotation.UiThread;

public interface OnAutocompleteSuggestionsReceivedCallback {
    /**
     * Called when search autocomplete suggestions are returned from an IAutoCompleteProvider
     ***/
    @UiThread
    void onAutocompleteSuggestionsReceived(String[] suggestions);
}
