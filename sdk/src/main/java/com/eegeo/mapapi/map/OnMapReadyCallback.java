package com.eegeo.mapapi.map;

import androidx.annotation.UiThread;

import com.eegeo.mapapi.EegeoMap;

/**
 * Defines the signature for a method that is called when the EegeoMap object has been created and
 * is ready to call.
 */
public interface OnMapReadyCallback {
    /**
     * Called when the EegeoMap object is ready to use.
     *
     * @param map The EegeoMap object, the main class for interacting with the map.
     */
    @UiThread
    void onMapReady(EegeoMap map);
}
