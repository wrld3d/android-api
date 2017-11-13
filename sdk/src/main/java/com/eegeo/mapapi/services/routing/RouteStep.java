package com.eegeo.mapapi.services.routing;

import com.eegeo.mapapi.geometry.LatLng;


public class RouteStep {

    public final LatLng latLng;

    RouteStep(final LatLng latLng) {
        this.latLng = latLng;
    }
}

