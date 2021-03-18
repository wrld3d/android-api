package com.eegeo.mapapi.services.poi;

import androidx.annotation.UiThread;


/**
 * A listener interface for receiving the results of a completed POI search.
 *
 * An object implementing this should be set on the TextSearchOptions , TagSearchOptions , or AutocompleteOptions provided to a search method.
 */
public interface OnPoiSearchCompletedListener {

    /**
     * Called when a POI search completes.
     *
     * @param response The response to the search. If the search was successful, this will contain search results.
     */
    @UiThread
    void onPoiSearchCompleted(PoiSearchResponse response);

}
