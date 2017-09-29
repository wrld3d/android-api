package com.eegeo.mapapi.buildings;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import com.eegeo.mapapi.geometry.LatLng;

public class BuildingDimensions {
    public final double baseAltitude;
    public final double topAltitude;
    public final LatLng centroid;

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
