package com.eegeo.mapapi.services.routing;

import com.eegeo.mapapi.geometry.LatLng;
import java.util.List;


/**
 * A single step of a Route.
 */
public class RouteStep {

    /**
     * A List of the individual LatLng points that make up this step. This can be a single point if no distance was covered, for example a RouteStep may indicate departure or arrival with a single point.
     */
    public final List<LatLng> path;

    /**
     * The directions associated with this step.
     */
    public final RouteDirections directions;

    /**
     * The mode of transport for this step.
     */
    public final TransportationMode mode;

    /**
     * Whether this step is indoors or not.
     */
    public final boolean isIndoors;

    /**
     * If indoors, the ID of the indoor map this step is inside.
     */
    public final String indoorId;

    /**
     * If indoors, the ID of the floor this step is on.
     */
    public final int indoorFloorId;

    /**
     * If indoors, this step represents moving between the floors of the previous and next steps
     */
    public final boolean isMultiFloor;

    /**
     * The estimated time this step will take to travel in seconds.
     */
    public final double duration;

    /**
     * The estimated distance this step covers in meters.
     */
    public final double distance;

    /**
     * If available, the name of the location this step is on.
     */
    public final String stepName;

    RouteStep(
            final List<LatLng> path,
            final RouteDirections directions,
            final TransportationMode mode,
            final boolean isIndoors,
            final String indoorId,
            final int indoorFloorId,
            final boolean isMultiFloor,
            final double duration,
            final double distance,
            final String stepName)
    {
        this.path = path;
        this.directions = directions;
        this.mode = mode;
        this.isIndoors = isIndoors;
        this.indoorId = indoorId;
        this.indoorFloorId = indoorFloorId;
        this.isMultiFloor = isMultiFloor;
        this.duration = duration;
        this.distance = distance;
        this.stepName = stepName;
    }
}

