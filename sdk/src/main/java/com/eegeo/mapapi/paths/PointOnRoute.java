package com.eegeo.mapapi.paths;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

public final class PointOnRoute {

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
    public final double fractionAlongRouteStep;

    /**
     * How far along the current Section of the route that the result point has travelled, as a fractional value from 0.0 to 1.0
     */
    public final double fractionAlongRouteSection;

    /**
     * How far along the entire route that the result point has travelled, as a fractional value from 0.0 to 1.0
     */
    public final double fractionAlongRoute;

    /**
     * The index of the route Step that the result point currently lies on.
     */
    public final int routeStepIndex;

    /**
     * The index of the route Section that the result point currently lies on.
     */
    public final int routeSectionIndex;

    /**
     * True if the result is valid: a PointOnRoute may be invalid if an outdoor route is provided and is tested against an indoor point, and vice versa.
     */
    public final boolean validResult;

    /**
     * Vertex index where the path segment starts for the projected point. Can be used to separate traversed path.
     */
    public final int pathSegmentStartVertexIndex;

    /**
     * @eegeo.internal
     */
    @UiThread
    public PointOnRoute(
            @NonNull LatLng resultPoint,
            @NonNull LatLng inputPoint,
                     double distanceFromInputPoint,
                     double fractionAlongRouteStep,
                     double fractionAlongRouteSection,
                     double fractionAlongRoute,
                     int routeSectionIndex,
                     int routeStepIndex,
                     boolean validResult,
                     int pathSegmentStartVertexIndex
    ) {
        this.resultPoint = resultPoint;
        this.inputPoint = inputPoint;
        this.distanceFromInputPoint = distanceFromInputPoint;
        this.fractionAlongRouteStep = fractionAlongRouteStep;
        this.fractionAlongRouteSection = fractionAlongRouteSection;
        this.fractionAlongRoute = fractionAlongRoute;
        this.routeSectionIndex = routeSectionIndex;
        this.routeStepIndex = routeStepIndex;
        this.validResult = validResult;
        this.pathSegmentStartVertexIndex = pathSegmentStartVertexIndex;
    }
}
