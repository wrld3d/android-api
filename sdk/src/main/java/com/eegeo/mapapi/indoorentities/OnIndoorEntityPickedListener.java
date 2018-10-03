package com.eegeo.mapapi.indoorentities;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when one or more indoor map entities are tapped or clicked.
 */
public interface OnIndoorEntityPickedListener {
    /**
     * Called when one or more indoor map entities have been tapped or clicked.
     *
     * @param message The IndoorEntityPickedMessage that contains information about the tapped/clicked entities.
     */
    @UiThread
    void onIndoorEntityPicked(IndoorEntityPickedMessage message);
}
