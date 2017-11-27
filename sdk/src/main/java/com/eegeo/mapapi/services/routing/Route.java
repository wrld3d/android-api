package com.eegeo.mapapi.services.routing;

import java.util.List;


/**
 * An object representing a route on the map.
 */
public class Route {

    /**
     * A List of the different RouteSection objects which make up this route.
     */
    public final List<RouteSection> sections;

    /**
     * The estimated time this route will take to travel in seconds.
     */
    public final double duration;

    /**
     * The estimated distance this route covers in meters.
     */
    public final double distance;

    Route(final List<RouteSection> sections, final double duration, final double distance) {
        this.sections = sections;
        this.duration = duration;
        this.distance = distance;
    }
}

