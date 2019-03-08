package com.eegeo.mapapi.indoors;

/**
 * Interface to provide notification of an indoor map unloading.
 */
public interface OnIndoorMapUnloadedListener {
    /**
     * Method called when an indoor map is unloaded via the map streaming system.
     */
    void onIndoorMapUnloaded(final String indoorMapId);
}
