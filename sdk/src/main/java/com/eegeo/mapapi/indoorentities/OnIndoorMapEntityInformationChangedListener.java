package com.eegeo.mapapi.indoorentities;

import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when an IndoorMapEntityInformation object has
 * been updated. This can occur as map tiles stream in, causing additional indoor map entities to
 * be present.
 */
public interface OnIndoorMapEntityInformationChangedListener {
    /**
     * Called when an IndoorMapEntityInformation object has changed
     *
     * @param indoorMapEntityInformation The IndoorMapEntityInformation object that has changed.
     */
    @UiThread
    void onIndoorMapEntityInformationChanged(IndoorMapEntityInformation indoorMapEntityInformation);
}
