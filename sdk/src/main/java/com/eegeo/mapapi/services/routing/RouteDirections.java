package com.eegeo.mapapi.services.routing;

import com.eegeo.mapapi.geometry.LatLng;


public class RouteDirections {

    public final String type;
    public final String modifier;
    public final LatLng location;
    public final double bearingbefore;
    public final double bearingafter;

    RouteDirections(
            final String type,
            final String modifier,
            final LatLng location,
            final double bearingbefore,
            final double bearingafter)
    {
        this.type = type;
        this.modifier = modifier;
        this.location = location;
        this.bearingbefore = bearingbefore;
        this.bearingafter = bearingafter;
    }
}

