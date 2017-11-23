package com.eegeo.mapapi.services.routing;

import com.eegeo.mapapi.geometry.LatLng;
import java.util.List;


public class RouteStep {

    public final List<LatLng> path;
    public final RouteDirections directions;
    public final TransportationMode mode;
    public final boolean isIndoors;
    public final String indoorId;
    public final int floorIndex;
    public final double duration;
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

