package com.eegeo.mapapi.pointonpath;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

public final class PointOnPath {

    /**
     * A point on the Route that is closest to the input point.
     */
    public final LatLng resultPoint;

    /**
     * The original target point tested against.
     */
    public final LatLng inputPoint;

    /**
     * The distance (in ECEF space) that the result point is from the input point.
     */
    public final double distanceFromInputPoint;

    /**
     * How far along the current Step of the route that the result point has travelled, as a fractional value from 0.0 to 1.0
     */
    public final double fractionAlongPath;

    /**
     * The index of the start of the path segment that the result point currently lies on.
     */
    public final int indexOfPathSegmentStartVertex;

    /**
     * The index of the end of the path segment that the result point currently lies on
     */
    public final int indexOfPathSegmentEndVertex;

    /**
     * @eegeo.internal
     */
    @UiThread
    public PointOnPath(
            @NonNull LatLng resultPoint,
            @NonNull LatLng inputPoint,
            double distanceFromInputPoint,
            double fractionAlongPath,
            int indexOfPathSegmentStartVertex,
            int indexOfPathSegmentEndVertex
    ) {
        this.resultPoint = resultPoint;
        this.inputPoint = inputPoint;
        this.distanceFromInputPoint = distanceFromInputPoint;
        this.fractionAlongPath = fractionAlongPath;
        this.indexOfPathSegmentStartVertex = indexOfPathSegmentStartVertex;
        this.indexOfPathSegmentEndVertex = indexOfPathSegmentEndVertex;
    }
}

