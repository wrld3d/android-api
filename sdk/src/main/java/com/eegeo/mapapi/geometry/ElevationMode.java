package com.eegeo.mapapi.geometry;

/**
 * Specifies how the elevation property of shape and Marker objects is interpreted.
 */
public enum ElevationMode {
    /**
     * The elevation property is interpreted as an absolute altitude above mean sea level, in
     * meters.
     */
    HeightAboveSeaLevel,

    /**
     * The elevation property is interpreted as a height relative to the map's terrain, in meters.
     */
    HeightAboveGround
}
