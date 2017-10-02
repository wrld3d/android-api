package com.eegeo.mapapi.buildings;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import com.eegeo.mapapi.geometry.LatLng;

/**
 * Represents dimensional information about a building on the map.
 */
public class BuildingDimensions {

    /**
     * The altitude of the building’s baseline - nominally at local ground level.
     */
    public final double baseAltitude;

    /**
     * The altitude of the building’s highest point.
     */
    public final double topAltitude;

    /**
     * The centroid of the building in plan view.
     */
    public final LatLng centroid;

    /**
     * @eegeo.internal
     */
    @UiThread
    public BuildingDimensions(
            double baseAltitude,
            double topAltitude,
            @NonNull LatLng centroid
    ) {
        this.baseAltitude = baseAltitude;
        this.topAltitude = topAltitude;
        this.centroid = centroid;
    }


}
