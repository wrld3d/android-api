package com.eegeo.mapapi.services.routing;

import java.util.List;


/**
 * The data for a single POI, returned by a POI search.
 */
public class RoutingQueryResult {

    public final List<RouteStep> steps;

    RoutingQueryResult(List<RouteStep> steps)
    {
        this.steps = steps;
    }
}

