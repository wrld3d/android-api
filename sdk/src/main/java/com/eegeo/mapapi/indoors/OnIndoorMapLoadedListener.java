package com.eegeo.mapapi.indoors;

/**
 * Interface to provide notification of an indoor map loading.
 */
public interface OnIndoorMapLoadedListener {
    /**
     * Method called when an indoor map is loaded via the map streaming system.
     */
    void onIndoorMapLoaded(final String indoorMapId);
}
