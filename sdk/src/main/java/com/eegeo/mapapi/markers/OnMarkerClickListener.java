package com.eegeo.mapapi.markers;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when a marker is tapped or clicked.
 */
public interface OnMarkerClickListener {
    /**
     * Called when a marker has been tapped or clicked.
     *
     * @param marker The Marker object that has been clicked.
     */
    @UiThread
    void onMarkerClick(Marker marker);
}
