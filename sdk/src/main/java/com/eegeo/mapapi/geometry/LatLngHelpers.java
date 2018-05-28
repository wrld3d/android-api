package com.eegeo.mapapi.geometry;

import java.util.List;

public class LatLngHelpers {

    static public double[] pointsToArray(List<LatLng> points) {
        final int pointCount = points.size();
        double[] coords = new double[pointCount * 2];
        for (int i = 0; i < pointCount; ++i) {
            coords[i * 2] = points.get(i).latitude;
            coords[i * 2 + 1] = points.get(i).longitude;
        }
        return coords;
    }
}
