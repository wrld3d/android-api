package com.eegeo.mapapi.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import android.support.annotation.UiThread;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.polylines.Polyline;
import com.eegeo.mapapi.polylines.PolylineOptions;
import com.eegeo.mapapi.services.routing.*;


public class RouteView {

    private EegeoMap m_map = null;
    private Route m_route = null;
    private List<Polyline> m_polylines = new ArrayList();
    private boolean m_currentlyOnMap = false;

    private float m_width;
    private int m_colorARGB;
    private int m_forwardPathColorARGB;
    private float m_miterLimit;

    private Map<Integer, List<RoutingPolylineCreateParams>> m_routeStepToPolylineCreateParams = new HashMap<>();

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
        this.m_forwardPathColorARGB = options.getForwardPathColor();
        this.m_miterLimit = options.getMiterLimit();
        addToMap();
    }


    /**
     * Add this RouteView back on to the map, if it has been removed.
     */
    public void addToMap() {
        int flattenedStepIndex = 0;
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

                    addLineCreationParamsForStep(step, stepBefore.indoorFloorId, stepAfter.indoorFloorId, flattenedStepIndex, m_colorARGB);
                } else {
                    addLineCreationParamsForStep(step, flattenedStepIndex);
                }
                flattenedStepIndex++;
            }
        }

        refreshPolylines();
        
        m_currentlyOnMap = true;
    }

    private void addLineCreationParamsForStep(RouteStep routeStep, int flattenedStepIndex) {
        if(routeStep.path.size() < 2) {
            return;
        }
        m_routeStepToPolylineCreateParams.put(flattenedStepIndex, RouteViewHelper.createLinesForRouteDirection(routeStep, m_colorARGB));
    }

    private void addLineCreationParamsForStep(RouteStep routeStep, int stepBefore, int stepAfter, int flattenedStepIndex, int color) {
        if(routeStep.path.size() < 2) {
            return;
        }
        m_routeStepToPolylineCreateParams.put(flattenedStepIndex, RouteViewHelper.createLinesForFloorTransition(routeStep, stepBefore, stepAfter, color));
    }

    private void addLineCreationParamsForStep(RouteStep routeStep, int stepIndex, LatLng closestPointOnPath, int splitIndex) {
        if(routeStep.path.size() < 2) {
            return;
        }
        m_routeStepToPolylineCreateParams.put(stepIndex, RouteViewHelper.createLinesForRouteDirection(routeStep, m_forwardPathColorARGB ,m_colorARGB, splitIndex, closestPointOnPath));
    }

    private void refreshPolylines() {
        removeFromMap();

        List<RoutingPolylineCreateParams> allPolylineCreateParams = new ArrayList<>();

        for(int i=0; i< m_routeStepToPolylineCreateParams.size(); i++) {
            allPolylineCreateParams.addAll(m_routeStepToPolylineCreateParams.get(i));
        }

        List<PolylineOptions> polyLineOptionsList = RouteViewAmalgamationHelper.createPolylines(allPolylineCreateParams, m_width, m_miterLimit);

        for(PolylineOptions polyLineOption: polyLineOptionsList) {
            Polyline routeLine = m_map.addPolyline(polyLineOption);
            m_polylines.add(routeLine);
        }
    }

    /**
     * Update the progress of turn by turn navigation on route.
     *
     * @param sectionIndex The index of current RouteSection.
     * @param stepIndex The index of current RouteStep.
     * @param closestPointOnRoute Closest point on the route in PointOnRoute.
     * @param indexOfPathSegmentStartVertex Vertex index where the path segment starts for the projected point. Can be used to separate traversed path.
     */

    public void updateRouteProgress(int sectionIndex, int stepIndex, LatLng closestPointOnRoute, int indexOfPathSegmentStartVertex) {
        removeFromMap();
        int flattenedStepIndex = 0;
        for (int x=0; x<m_route.sections.size(); ++x) {
            List<RouteStep> steps = m_route.sections.get(x).steps;

            for (int i=0; i<steps.size(); ++i) {
                RouteStep step = steps.get(i);
                boolean isActiveStep = sectionIndex == x && stepIndex == i;

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

                    if(isActiveStep) {
                        boolean hasReachedEnd = indexOfPathSegmentStartVertex == (step.path.size()-1);
                        addLineCreationParamsForStep(step, stepBefore.indoorFloorId, stepAfter.indoorFloorId, flattenedStepIndex, (hasReachedEnd ? m_colorARGB : m_forwardPathColorARGB));

                    } else {
                        addLineCreationParamsForStep(step, stepBefore.indoorFloorId, stepAfter.indoorFloorId, flattenedStepIndex, m_colorARGB);
                    }
                } else {
                    if(isActiveStep) {
                        addLineCreationParamsForStep(step, flattenedStepIndex, closestPointOnRoute, indexOfPathSegmentStartVertex);
                    } else {
                        addLineCreationParamsForStep(step, flattenedStepIndex);
                    }
                }
                flattenedStepIndex++;
            }
        }
        refreshPolylines();
    }

    /**
     * Remove this RouteView from the map.
     */
    public void removeFromMap() {
        for (Polyline polyline: m_polylines) {
            m_map.removePolyline(polyline);
        }
        m_polylines.clear();
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

