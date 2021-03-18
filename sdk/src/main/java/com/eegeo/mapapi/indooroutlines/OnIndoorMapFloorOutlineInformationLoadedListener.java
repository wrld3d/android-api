package com.eegeo.mapapi.indooroutlines;


import androidx.annotation.UiThread;

/**
 * Defines the signature for a method that is called when an IndoorMapFloorOutlineInformation object has
 * finished loading.
 */
public interface OnIndoorMapFloorOutlineInformationLoadedListener {
    /**
     * Called when an IndoorMapFloorOutlineInformation object has loaded
     *
     * @param indoorMapFloorOutlineInformation The IndoorMapFloorOutlineInformation object that has finished loading.
     */
    @UiThread
    void onIndoorMapFloorOutlineInformationLoaded(IndoorMapFloorOutlineInformation indoorMapFloorOutlineInformation);
}
