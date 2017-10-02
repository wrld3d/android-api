package com.eegeo.mapapi.buildings;

import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

/**
 * Represents a building or part of building as a polygon with minimum and maximum altitudes.
 * This can be used to construct an extruded polygon (prism) to visually represent the building.
 * Complex buildings may be made up of multiple BuildingContours.
 */
public class BuildingContour {

    /**
     * The minimum altitude above sea level.
     */
    public final double bottomAltitude;

    /**
     * The maximum altitude above sea level.
     */
    public final double topAltitude;

    /**
     * The vertices of the building outline polygon, ordered clockwise from above.
     */
    public final LatLng[] points;

    /**
     * @eegeo.internal
     */
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
