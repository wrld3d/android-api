package com.eegeo.mapapi.widgets;

import android.location.Location;
import com.eegeo.mapapi.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;


public class RouteViewHelper {
    public static List<LatLng> removeCoincidentPoints(List<LatLng> coordinates) {
        List<LatLng> uniqueCoordinates = new ArrayList<>();

        for(int i=0; i<coordinates.size(); i++) {
            LatLng firstLocation = coordinates.get(i);
            boolean isUnique = true;
            for(int j=i+1; j<coordinates.size(); j++) {
                LatLng secondLocation = coordinates.get(j);
                if (areApproximatelyEqual(firstLocation, secondLocation)) {
                    isUnique = false;
                }
            }
            if (isUnique) {
                uniqueCoordinates.add(firstLocation);
            }
        }
        return uniqueCoordinates;
    }

    public static boolean areApproximatelyEqual(LatLng firstLocation, LatLng secondLocation) {
        final double epsilonSq = 1e-6;
        float[] results = new float[1];
        Location.distanceBetween(firstLocation.latitude, firstLocation.longitude, secondLocation.latitude, secondLocation.longitude, results);
        return results[0] <= epsilonSq;
    }
}
