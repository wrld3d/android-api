package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a polygon that makes up the outline for an indoor map floor.
 * This correspond to outline within a level GeoJSON in an indoor map submission via the WRLD Indoor Map REST API.
 * See [https://github.com/wrld3d/wrld-indoor-maps-api/blob/master/FORMAT.md](https://github.com/wrld3d/wrld-indoor-maps-api/blob/master/FORMAT.md)
 */
public class IndoorFloorOutlinePolygon {
    /**
     * The outer ring of this polygon.
     * This is a list of points that make up the outer perimeter of a polygon.
     */
    public final IndoorFloorOutlinePolygonRing outerRing;
    /**
     * The inner rings of this polygon.
     * These are the list of points that make up the holes within a polygon.
     */
    public final List<IndoorFloorOutlinePolygonRing> innerRings;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorFloorOutlinePolygon(
            @NonNull IndoorFloorOutlinePolygonRing outerRing,
            @NonNull IndoorFloorOutlinePolygonRing innerRings[]
    ) {
        this.outerRing = outerRing;
        this.innerRings = Arrays.asList(innerRings);
    }
}
