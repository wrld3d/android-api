package com.eegeo.mapapi.map;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when the eeGeo map has completed streaming
 * resources needed to display the map for the initial map viewpoint.
 * <br>
 * An example use of this would be in an app that initially displays a loading screen, which is then
 * hidden once initial streaming is complete.
 */
public interface OnInitialStreamingCompleteListener {
    /**
     * Called when the map has completed streaming resources for the initial map viewpoint.
     */
    @UiThread
    void onInitialStreamingComplete();
}
