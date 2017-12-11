package com.eegeo.mapapi.services.routing;

import java.util.List;


/**
 * A section of a Route. For example, the portion from a point indoors to the exit.
 */
public class RouteSection {

    /**
     * A List of the RouteStep objects that make up this section.
     */
    public final List<RouteStep> steps;

    /**
     * The estimated time this section will take to travel in seconds.
     */
    public final double duration;

    /**
     * The estimated distance this section covers in meters.
     */
    public final double distance;

    RouteSection(final List<RouteStep> steps, final double duration, final double distance) {
        this.steps = steps;
        this.duration = duration;
        this.distance = distance;
    }
}

