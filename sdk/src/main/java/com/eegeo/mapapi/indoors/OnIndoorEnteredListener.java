package com.eegeo.mapapi.indoors;

import android.support.annotation.UiThread;

/**
 * Interface for objects which act when the user enters an indoor map.
 */
public interface OnIndoorEnteredListener {
    /**
     * Method called when an indoor map is entered.
     */
    @UiThread
    void onIndoorEntered();
}
