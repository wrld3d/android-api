package com.eegeo.mapapi.indooroutlines;


import android.support.annotation.UiThread;

/**
 * Defines the signature for a method that is called when an IndoorFloorOutlineInformation object has
 * finished loading.
 */
public interface OnIndoorFloorOutlineInformationLoadedListener {
    /**
     * Called when an IndoorFloorOutlineInformation object has loaded
     *
     * @param indoorFloorOutlineInformation The IndoorFloorOutlineInformation object that has finished loading.
     */
    @UiThread
    void onIndoorMapEntityInformationLoaded(IndoorFloorOutlineInformation indoorFloorOutlineInformation);
}
