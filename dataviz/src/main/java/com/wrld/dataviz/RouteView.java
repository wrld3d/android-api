package com.wrld.dataviz;

import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.polylines.Polyline;
import com.eegeo.mapapi.polylines.PolylineOptions;
import com.eegeo.mapapi.services.routing.*;


public class RouteView {

    private EegeoMap map = null;
    private Route route = null;
    private List<Polyline> polylines = new ArrayList();

    public RouteView(EegeoMap map, Route route) {
        this.map = map;
        this.route = route;
        addToMap();
    }

    private Polyline makeVerticalLine(RouteStep step, int floor, int direction) {
        PolylineOptions options = new PolylineOptions()
            .color(ColorUtils.setAlphaComponent(Color.RED, 128))
            .indoor(step.indoorId, floor)
            .add(step.path.get(0), 0.0)
            .add(step.path.get(1), 5.0 * direction);

        return map.addPolyline(options);
    }

    public void addToMap() {
        for (RouteSection section: route.sections) {
            List<RouteStep> steps = section.steps;

            for (int i=0; i<steps.size(); ++i) {
                RouteStep step = steps.get(i);

                if (step.path.size() < 2) {
                    continue;
                }

                if (step.isMultiFloor) {
                    if (i == 0 || i == (steps.size() - 1) || !step.isIndoors) {
                        continue;
                    }

                    int floorBefore = steps.get(i-1).indoorFloorId;
                    int floorAfter = steps.get(i+1).indoorFloorId;
                    int direction = Integer.signum(floorAfter - floorBefore);

                    polylines.add(makeVerticalLine(step, floorBefore, direction));

                    int middleFloors = Math.abs(floorAfter - floorBefore) - 1;
                    for (int j = 0; j < middleFloors; ++j) {
                        int floorId = floorBefore + (j + 1) * direction;
                        polylines.add(makeVerticalLine(step, floorId, 1));
                    }

                    polylines.add(makeVerticalLine(step, floorAfter, -direction));
                }
                else {
                    PolylineOptions options = new PolylineOptions()
                        .color(ColorUtils.setAlphaComponent(Color.RED, 128));

                    if (step.isIndoors) {
                        options.indoor(step.indoorId, step.indoorFloorId);
                    }

                    for (LatLng point: step.path) {
                        options.add(point);
                    }

                    Polyline routeLine = map.addPolyline(options);
                    polylines.add(routeLine);
                }
            }
        }
    }

    public void removeFromMap() {
        for (Polyline polyline: polylines) {
            map.removePolyline(polyline);
        }
    }
}
