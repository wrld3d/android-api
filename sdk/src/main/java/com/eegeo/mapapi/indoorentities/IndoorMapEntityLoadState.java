package com.eegeo.mapapi.indoorentities;

/**
* Represents the current map tiles loading state for an indoor map.
*/
public enum IndoorMapEntityLoadState {
    /**
     * The indoor map is not loaded.
     */
    None,

    /**
     * Some map tiles for the indoor map are loaded.
     */
    Partial,

    /**
     * All map tiles for the indoor map are loaded.
     */
    Complete
}
