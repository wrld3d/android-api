package com.eegeo.mapapi.indoors;

import android.support.annotation.UiThread;

/**
 * Interface for objects which act when the user fails to enter an indoor map.
 */
public interface OnIndoorEnterFailedListener {
    /**
     * Method called when an indoor map enter request failed.
     */
    @UiThread
    void OnIndoorEnterFailed(final String indoorMapId);
}
