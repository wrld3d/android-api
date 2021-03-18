package com.eegeo.mapapi.positioner;

import androidx.annotation.UiThread;


/**
 * An interface that may be implemented and supplied to PositionerOptions in order to
 * receive notification that a Positioner has changed screen-space position.
 */
public interface OnPositionerChangedListener {
    /**
     * Called when a Positioner object has changed. Its updated position may be obtained with
     * positioner.getScreenPointOrNull()
     * @param positioner The Positioner object that has changed.
     */
    @UiThread
    void onPositionerChanged(Positioner positioner);
}
