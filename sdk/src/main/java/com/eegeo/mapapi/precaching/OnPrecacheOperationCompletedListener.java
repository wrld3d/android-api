package com.eegeo.mapapi.precaching;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when a precache operation is completed or
 * cancelled.
 */
public interface OnPrecacheOperationCompletedListener
{
    /**
     * A method to be executed when a precache operation completes.
     *
     * @param result the completed precache operation's status
     */
    @UiThread
    void onPrecacheOperationCompleted(PrecacheOperationResult result);
}

