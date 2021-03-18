package com.eegeo.mapapi.indooroutlines;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the points that make up a polygon ring.
 */
public class IndoorMapFloorOutlinePolygonRing {

    /**
     * A list of point that represent the ring of a polygon.
     */
    public final List<LatLng> latLngPoints;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapFloorOutlinePolygonRing(@NonNull LatLng latLngPoints[]) {
        this.latLngPoints = Arrays.asList(latLngPoints);
    }
}
