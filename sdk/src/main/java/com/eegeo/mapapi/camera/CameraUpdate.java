package com.eegeo.mapapi.camera;

/**
 * Interface for objects which specify a change to the camera.
 */
public interface CameraUpdate {
    /**
     * Gets the update type.
     *
     * @return The update type supported by the object.
     */
    CameraUpdateType getUpdateType();

    /**
     * Enumeration of available camera update types.
     */
    enum CameraUpdateType {
        /**
         * The change is specified as a new position for the camera.
         */
        CameraPosition,
        /**
         * The change is specified as a LatLngBounds area to be framed by the camera.
         */
        LatLngBounds
    }
}
