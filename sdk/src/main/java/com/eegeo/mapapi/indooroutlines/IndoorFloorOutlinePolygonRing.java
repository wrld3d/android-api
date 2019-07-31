package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the points that make up a polygon ring.
 */
public class IndoorFloorOutlinePolygonRing {

    /**
     * A list of point that represent the ring of a polygon.
     */
    public final List<LatLng> latLngPoints;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorFloorOutlinePolygonRing(@NonNull LatLng latLngPoints[]) {
        this.latLngPoints = Arrays.asList(latLngPoints);
    }
}
