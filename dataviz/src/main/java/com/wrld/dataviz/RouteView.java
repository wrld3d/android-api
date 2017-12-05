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

    public void addToMap() {
        for (RouteSection section: route.sections) {
            for (RouteStep step: section.steps) {
                if (step.path.size() < 2) {
                    continue;
                }

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

    public void removeFromMap() {
        for (Polyline polyline: polylines) {
            map.removePolyline(polyline);
        }
    }
}
