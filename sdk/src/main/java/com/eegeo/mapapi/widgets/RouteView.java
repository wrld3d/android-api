package com.eegeo.mapapi.widgets;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.polylines.Polyline;
import com.eegeo.mapapi.polylines.PolylineOptions;
import com.eegeo.mapapi.services.routing.*;


public class RouteView {

    private static double VERTICAL_LINE_HEIGHT = 5.0;

    private EegeoMap m_map = null;
    private Route m_route = null;
    private List<Polyline> m_polylines = new ArrayList();
    private boolean m_currentlyOnMap = false;

    private float m_width;
    private int m_colorARGB;
    private int m_activeColorARGB;
    private float m_miterLimit;


    /**
     * Create a new RouteView for the given route and options, and add it to the map.
     *
     * @param map The EegeoMap to draw this RouteView on.
     * @param route The Route to display.
     * @param options Options for styling the route.
     */
    public RouteView(EegeoMap map, Route route, RouteViewOptions options) {
        this.m_map = map;
        this.m_route = route;
        this.m_width = options.getWidth();
        this.m_colorARGB = options.getColor();
        this.m_activeColorARGB = options.getActiveColor();
        this.m_miterLimit = options.getMiterLimit();
        addToMap();
    }


    /**
     * Add this RouteView back on to the map, if it has been removed.
     */
    public void addToMap() {
        for (RouteSection section: m_route.sections) {
            List<RouteStep> steps = section.steps;

            for (int i=0; i<steps.size(); ++i) {
                RouteStep step = steps.get(i);

                if (step.path.size() < 2) {
                    continue;
                }

                if (step.isMultiFloor) {
                    boolean isValidTransition = i > 0 && i < (steps.size() - 1) && step.isIndoors;

                    if (!isValidTransition) {
                        continue;
                    }

                    RouteStep stepBefore = steps.get(i-1);
                    RouteStep stepAfter = steps.get(i+1);

                    addLinesForFloorTransition(step, stepBefore, stepAfter);
                }
                else {
                    addLinesForRouteStep(step);
                }
            }
        }

        m_currentlyOnMap = true;
    }

    private PolylineOptions basePolylineOptions() {
        return new PolylineOptions()
            .color(m_colorARGB)
            .width(m_width)
            .miterLimit(m_miterLimit);
    }

    private Polyline makeVerticalLine(RouteStep step, int floor, double height) {
        PolylineOptions options = basePolylineOptions()
            .indoor(step.indoorId, floor)
            .add(step.path.get(0), 0.0)
            .add(step.path.get(1), height);

        return m_map.addPolyline(options);
    }

    private void addLinesForRouteStep(RouteStep step) {
        PolylineOptions options = basePolylineOptions();

        if (step.isIndoors) {
            options.indoor(step.indoorId, step.indoorFloorId);
        }

        for (LatLng point: step.path) {
            options.add(point);
        }

        Polyline routeLine = m_map.addPolyline(options);
        m_polylines.add(routeLine);
    }

    private void addLinesForFloorTransition(RouteStep step, RouteStep stepBefore, RouteStep stepAfter) {
        int floorBefore = stepBefore.indoorFloorId;
        int floorAfter = stepAfter.indoorFloorId;
        double lineHeight = (floorAfter > floorBefore) ? VERTICAL_LINE_HEIGHT : -VERTICAL_LINE_HEIGHT;

        m_polylines.add(makeVerticalLine(step, floorBefore, lineHeight));
        m_polylines.add(makeVerticalLine(step, floorAfter, -lineHeight));
    }

    /**
     * Update the progress of turn by turn navigation on route.
     */

    public void updateRouteProgress(int sectionIndex, int stepIndex, LatLng closestPointOnPath, int indexOfPathSegmentStartVertex) {
        for (Polyline polyline: m_polylines) {
            m_map.removePolyline(polyline);
        }

        for (int x=0; x<m_route.sections.size(); ++x) {
            List<RouteStep> steps = m_route.sections.get(x).steps;

            for (int i=0; i<steps.size(); ++i) {
                RouteStep step = steps.get(i);
                if (step.path.size() < 2) {
                    continue;
                }
                if (step.isMultiFloor) {
                    boolean isValidTransition = i > 0 && i < (steps.size() - 1) && step.isIndoors;
                    if (!isValidTransition) {
                        continue;
                    }
                    RouteStep stepBefore = steps.get(i-1);
                    RouteStep stepAfter = steps.get(i+1);
                    addLinesForFloorTransition(step, stepBefore, stepAfter);
                }
                else {
                    boolean isActiveStep = sectionIndex == x && stepIndex == i;
                    if(isActiveStep) {
                        addLinesForActiveRouteStep(step, indexOfPathSegmentStartVertex, closestPointOnPath);
                    } else {
                        addLinesForRouteStep(step);
                    }
                }
            }
        }
    }

    private PolylineOptions activePolylineOptions() {
        return new PolylineOptions()
                .color(m_activeColorARGB)
                .width(m_width)
                .miterLimit(m_miterLimit);
    }

    private void addLinesForActiveRouteStep(RouteStep step, int splitIndex, LatLng closestPointOnPath) {
        List<LatLng> backArray = new ArrayList<>();
        for(int i=0; i <=splitIndex; i++) {
            LatLng point = step.path.get(i);
            backArray.add(point);
        }
        backArray.add(closestPointOnPath);
        backArray = removeCoincidentPoints(backArray);

        if(backArray.size() >= 2) {
            PolylineOptions basePolylineOptions = basePolylineOptions();
            if (step.isIndoors) {
                basePolylineOptions.indoor(step.indoorId, step.indoorFloorId);
            }
            for(int i=0; i < backArray.size(); i++) {
                LatLng point = backArray.get(i);
                basePolylineOptions.add(point);
            }
            Polyline routeLine = m_map.addPolyline(basePolylineOptions);
            m_polylines.add(routeLine);
        }

        List<LatLng> forwardArray = new ArrayList<>();
        forwardArray.add(closestPointOnPath);
        for (int x=splitIndex+1; x < step.path.size(); x++) {
            LatLng point = step.path.get(x);
            forwardArray.add(point);
        }
        forwardArray = removeCoincidentPoints(forwardArray);

        if (forwardArray.size() >= 2) {
            PolylineOptions activePolylineOptions = activePolylineOptions();
            if (step.isIndoors) {
                activePolylineOptions.indoor(step.indoorId, step.indoorFloorId);
            }
            for(int i=0; i < forwardArray.size(); i++) {
                LatLng point = forwardArray.get(i);
                activePolylineOptions.add(point);
            }
            Polyline routeLineActive = m_map.addPolyline(activePolylineOptions);
            m_polylines.add(routeLineActive);
        }
    }

    private List<LatLng> removeCoincidentPoints(List<LatLng> coordinates) {
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

    private boolean areApproximatelyEqual(LatLng firstLocation, LatLng secondLocation) {
        final double epsilonSq = 1e-6;
        Location first = new Location("");
        first.setLatitude(firstLocation.latitude);
        first.setLongitude(firstLocation.longitude);

        Location second = new Location("");
        second.setLatitude(secondLocation.latitude);
        second.setLongitude(secondLocation.longitude);

        return first.distanceTo(second) <= epsilonSq;
    }
    /**
     * Remove this RouteView from the map.
     */
    public void removeFromMap() {
        for (Polyline polyline: m_polylines) {
            m_map.removePolyline(polyline);
        }

        m_currentlyOnMap = false;
    }


    /**
     * Sets the width of this RouteView's polylines.
     *
     * @param width The width of the polyline in screen pixels.
     */
    @UiThread
    public void setWidth(float width) {
        m_width = width;
        for (Polyline polyline: m_polylines) {
            polyline.setWidth(m_width);
        }
    }

    /**
     * Sets the color for this RouteView's polylines.
     *
     * @param color The color of the polyline as a 32-bit ARGB color.
     */
    @UiThread
    public void setColor(int color) {
        m_colorARGB = color;
        for (Polyline polyline: m_polylines) {
            polyline.setColor(m_colorARGB);
        }
    }

    /**
     * Sets the miter limit of this RouteView's polylines.
     *
     * @param miterLimit the miter limit, as a ratio between maximum allowed miter join diagonal
     *                   length and the line width.
     */
    @UiThread
    public void setMiterLimit(float miterLimit) {
        m_miterLimit = miterLimit;
        for (Polyline polyline: m_polylines) {
            polyline.setMiterLimit(m_miterLimit);
        }
    }
}

