package com.eegeo.mapapi.services.routing;

import com.eegeo.mapapi.geometry.LatLng;
import java.util.List;


/**
 * A single step of a Route.
 */
public class RouteStep {

    /**
     * A List of the individual LatLng points that make up this step.
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
     * If indoors, the index of the floor this step is on.
     */
    public final int floorIndex;

    /**
     * The estimated time this step will take to travel.
     */
    public final double duration;

    /**
     * The estimated distance this step covers.
     */
    public final double distance;

    RouteStep(
            final List<LatLng> path,
            final RouteDirections directions,
            final TransportationMode mode,
            final boolean isIndoors,
            final String indoorId,
            final int floorIndex,
            final double duration,
            final double distance)
    {
        this.path = path;
        this.directions = directions;
        this.mode = mode;
        this.isIndoors = isIndoors;
        this.indoorId = indoorId;
        this.floorIndex = floorIndex;
        this.duration = duration;
        this.distance = distance;
    }
}

