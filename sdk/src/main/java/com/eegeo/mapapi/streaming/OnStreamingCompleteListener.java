package com.eegeo.mapapi.streaming;

import androidx.annotation.UiThread;

import com.eegeo.mapapi.buildings.BuildingHighlight;

/**
 * An interface that may be implemented and supplied to BuildingHighlightOptions in order to
 * receive notification that BuildingInformation has been received.
 */
public interface OnStreamingCompleteListener {
    /**
     * Called when the streaming has been completed after a camera movement
     */
    @UiThread
    void onStreamingComplete();

}
