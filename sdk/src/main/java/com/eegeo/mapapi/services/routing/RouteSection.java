package com.eegeo.mapapi.services.routing;

import java.util.List;


public class RouteSection {

    public final List<RouteStep> steps;
    public final double duration;
    public final double distance;

    RouteSection(final List<RouteStep> steps, final double duration, final double distance) {
        this.steps = steps;
        this.duration = duration;
        this.distance = distance;
    }
}

