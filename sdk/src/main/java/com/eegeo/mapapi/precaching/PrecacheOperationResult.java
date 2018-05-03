package com.eegeo.mapapi.precaching;

import android.support.annotation.UiThread;

/**
 * A result of a precache operation.
 * Returned whenever a precache operation completes via OnPrecacheOperationCompletedListener.onPrecacheOperationCompleted
 */
public class PrecacheOperationResult {

    private boolean m_succeeded;

    PrecacheOperationResult(boolean succeeded) {
        m_succeeded = succeeded;
    }

    /**
     * @return A boolean indicating whether the precache operation succeeded or not.
     */
    @UiThread
    public boolean succeeded() {
        return m_succeeded;
    }
}