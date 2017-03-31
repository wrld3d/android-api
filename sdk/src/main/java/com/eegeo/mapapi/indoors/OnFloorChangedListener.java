package com.eegeo.mapapi.indoors;

/**
 * Interface for objects which act on floor change events for an indoor map.
 */
public interface OnFloorChangedListener {
    /**
     * Method called when the current floor changes in an indoor map.
     *
     * @param selectedFloor The index of the new current floor selection.
     */
    void onFloorChanged(int selectedFloor);
}
