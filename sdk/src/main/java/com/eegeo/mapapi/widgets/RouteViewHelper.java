package com.eegeo.mapapi.widgets;

import android.location.Location;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.services.routing.RouteStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class RouteViewHelper {

    final private static double VERTICAL_LINE_HEIGHT = 5.0;

    public static boolean areApproximatelyEqual(LatLng firstLocation, LatLng secondLocation) {
        final double epsilonSq = 1e-6;
        float[] results = new float[1];
        Location.distanceBetween(firstLocation.latitude, firstLocation.longitude, secondLocation.latitude, secondLocation.longitude, results);
        return results[0] <= epsilonSq;
    }

    public static boolean areCoordinateElevationPairApproximatelyEqual(LatLng latLngA, LatLng latLngB, Double perPointElevationA, Double perPointElevationB) {
        final double elevationEpsilon = 1e-3;

        if(!areApproximatelyEqual(latLngA, latLngB)) {
            return false;
        }
        return Math.abs(perPointElevationA - perPointElevationB) < elevationEpsilon;
    }

    public static void removeCoincidentPoints(List<LatLng> coordinates) {
        if(coordinates == null) {
            return;
        }

        Iterator<LatLng> it = coordinates.iterator();
        LatLng prev = null;
        LatLng current = it.next();
        while (it.hasNext()) {
            prev = current;
            current = it.next();
            if (areApproximatelyEqual(current, prev)) {
                it.remove();
            }
        }
    }

    public static void removeCoincidentPointsWithElevations(List<LatLng> coordinates, List<Double> perPointElevations) {
        Iterator<LatLng> coordinatesIt = coordinates.iterator();
        Iterator<Double> perPointIt = perPointElevations.iterator();
        LatLng prevLatLng = null;
        LatLng currentLatLng = coordinatesIt.next();
        Double prevPerPoint = null;
        Double currentPerPoint = perPointIt.next();
        while (coordinatesIt.hasNext() && perPointIt.hasNext()) {
            prevLatLng = currentLatLng;
            currentLatLng = coordinatesIt.next();
            prevPerPoint = currentPerPoint;
            currentPerPoint = perPointIt.next();

            if (areCoordinateElevationPairApproximatelyEqual(currentLatLng, prevLatLng, currentPerPoint, prevPerPoint)) {
                coordinatesIt.remove();
                perPointIt.remove();
            }
        }
    }

    public static RoutingPolylineCreateParams  makeNavRoutingPolylineCreateParams(List<LatLng> coordinates, int color, String indoorId, int indoorFloorId) {
        return new RoutingPolylineCreateParams(coordinates, color, indoorId, indoorFloorId, null);
    }

    public static RoutingPolylineCreateParams  makeNavRoutingPolylineCreateParams(List<LatLng> coordinates, int color, String indoorId, int indoorFloorId, double heightStart, double heightEnd) {
        return new RoutingPolylineCreateParams(coordinates, color, indoorId, indoorFloorId, Arrays.asList(heightStart, heightEnd));
    }

    public static List<RoutingPolylineCreateParams> createLinesForRouteDirection(RouteStep routeStep, int color) {
        List<RoutingPolylineCreateParams> results = new ArrayList<>();

        List<LatLng> pathCoordinates = new ArrayList<>();
        pathCoordinates.addAll(routeStep.path);
        RouteViewHelper.removeCoincidentPoints(pathCoordinates);
        if(routeStep.path.size() > 1) {
            RoutingPolylineCreateParams polylineCreateParams = makeNavRoutingPolylineCreateParams(pathCoordinates, color, routeStep.indoorId, routeStep.indoorFloorId);
            results.add(polylineCreateParams);
        }

        return results;
    }

    public static List<RoutingPolylineCreateParams> createLinesForRouteDirection(RouteStep routeStep, int forwardColor, int backwardColor, int splitIndex, LatLng closestPointOnPath) {
        int coordinatesSize = routeStep.path.size();
        boolean hasReachedEnd = splitIndex == (routeStep.path.size()-1);

        if (hasReachedEnd) {
            return createLinesForRouteDirection(routeStep, backwardColor);
        } else {
            List<RoutingPolylineCreateParams> results = new ArrayList<>();

            int forwardPathSize = coordinatesSize - (splitIndex + 1);
            int backwardPathSize = coordinatesSize - forwardPathSize;

            List<LatLng> forwardPath = new ArrayList<>(forwardPathSize);
            List<LatLng> backwardPath = new ArrayList<>(backwardPathSize);

            //Forward path starts with the split point
            forwardPath.add(closestPointOnPath);

            for (int i=0; i<coordinatesSize; i++) {
                if(i<=splitIndex) {
                    backwardPath.add(routeStep.path.get(i));
                } else {
                    forwardPath.add(routeStep.path.get(i));
                }
            }

            //Backward path ends with the split point
            backwardPath.add(closestPointOnPath);

            removeCoincidentPoints(backwardPath);
            removeCoincidentPoints(forwardPath);

            if(backwardPath.size() > 1) {
                results.add(makeNavRoutingPolylineCreateParams(backwardPath, backwardColor, routeStep.indoorId, routeStep.indoorFloorId));
            }

            if(forwardPath.size() > 1) {
                results.add(makeNavRoutingPolylineCreateParams(forwardPath, forwardColor, routeStep.indoorId, routeStep.indoorFloorId));
            }

            return results;
        }
    }

    public static List<RoutingPolylineCreateParams> createLinesForFloorTransition(RouteStep routeStep, int floorBefore, int floorAfter, int color) {
        double lineHeight = (floorAfter > floorBefore) ? VERTICAL_LINE_HEIGHT : -VERTICAL_LINE_HEIGHT;

        int coordinateCount = routeStep.path.size();

        List<LatLng> startCoords = new ArrayList<>(2);
        startCoords.add(routeStep.path.get(0));
        startCoords.add(routeStep.path.get(1));

        List<LatLng> endCoords = new ArrayList<>(2);
        endCoords.add(routeStep.path.get(coordinateCount-2));
        endCoords.add(routeStep.path.get(coordinateCount-1));

        List<RoutingPolylineCreateParams> results = new ArrayList<>(2);
        results.add(makeNavRoutingPolylineCreateParams(startCoords, color, routeStep.indoorId, floorBefore, 0.0, lineHeight));
        results.add(makeNavRoutingPolylineCreateParams(endCoords, color, routeStep.indoorId, floorAfter, -lineHeight, 0.0));

        return results;
    }
}
