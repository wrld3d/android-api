package com.eegeo.mapapi.services.mapscene;

import android.support.annotation.UiThread;

import com.eegeo.mapapi.services.poi.MapsceneRequestResponse;


/**
 * A listener interface for receiving the result of a completed Mapscene request.
 *
 * An object implementing this should be set on the MapsceneRequestOptions provided to the Mapscene request method.
 */
public interface OnMapsceneRequestCompletedListener {

    /**
     * Called when a Mapscene request completes.
     *
     * @param response The response to the request. If the request was successful, this will contain a Mapscene.
     */
    @UiThread
    void onMapsceneRequestCompleted(MapsceneRequestResponse response);

}
