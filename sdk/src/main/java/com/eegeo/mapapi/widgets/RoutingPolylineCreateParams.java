package com.eegeo.mapapi.widgets;

import com.eegeo.mapapi.geometry.LatLng;

import java.util.List;

class RoutingPolylineCreateParams {

    public List<LatLng> path;

    public boolean isForwardColor;

    public String indoorMapId;

    public int indoorFloorId;

    public List<Double> perPointElevations;

    public boolean isIndoor;

    RoutingPolylineCreateParams(
            List<LatLng> path,
            boolean isForwardColor,
            String indoorMapId,
            int indoorFloorId,
            List<Double> perPointElevations) {
        this.path = path;
        this.isForwardColor = isForwardColor;
        this.indoorMapId = indoorMapId;
        this.indoorFloorId = indoorFloorId;
        this.perPointElevations = perPointElevations;
        this.isIndoor = !indoorMapId.isEmpty();
    }
}
