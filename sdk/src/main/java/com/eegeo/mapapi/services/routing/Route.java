package com.eegeo.mapapi.services.routing;

import java.util.List;


public class Route {

    public final List<RouteSection> sections;
    public final double duration;
    public final double distance;

    Route(final List<RouteSection> sections, final double duration, final double distance) {
        this.sections = sections;
        this.duration = duration;
        this.distance = distance;
    }
}

