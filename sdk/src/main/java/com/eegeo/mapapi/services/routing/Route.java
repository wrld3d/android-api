package com.eegeo.mapapi.services.routing;

import com.eegeo.mapapi.geometry.LatLng;
import java.util.List;


public class Route {

    public final List<LatLng> overview;
    public final List<RouteLeg> legs;
    public final double duration;
    public final double distance;

    Route(final List<LatLng> overview, final List<RouteLeg> legs, final double duration, final double distance) {
        this.overview = overview;
        this.legs = legs;
        this.duration = duration;
        this.distance = distance;
    }
}

