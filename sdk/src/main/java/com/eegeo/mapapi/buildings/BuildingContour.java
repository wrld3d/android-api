package com.eegeo.mapapi.buildings;

import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

public class BuildingContour {
    public final double bottomAltitude;
    public final double topAltitude;
    public final LatLng[] points;

    @UiThread
    public BuildingContour(
            double bottomAltitude,
            double topAltitude,
            LatLng[] points
    ) {
        this.bottomAltitude = bottomAltitude;
        this.topAltitude = topAltitude;
        this.points = points;
    }

}
